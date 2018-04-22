package com.andrewvora.apps.lensemblem

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.widget.Toast
import com.andrewvora.apps.lensemblem.capturehistory.LatestScreenshot
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import com.andrewvora.apps.lensemblem.notifications.NotificationAction
import com.andrewvora.apps.lensemblem.notifications.NotificationHelper
import com.andrewvora.apps.lensemblem.ocr.OCRHeroProcessor
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.andrewvora.apps.lensemblem.statprocessing.IVProcessor
import javax.inject.Inject

/**
 * Background service that takes screenshots and processes them.
 * Created on 2/26/2018.
 * @author Andrew Vorakrajangthiti
 */
class LensEmblemService : Service() {

    enum class ServiceState {
        START,
        READY,
        PROCESSING
    }

    @Inject lateinit var screenshotHelper: ScreenshotHelper
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var heroProcessor: OCRHeroProcessor
    @Inject lateinit var ivProcessor: IVProcessor
    @Inject lateinit var heroesRepo: HeroesRepo
    @Inject lateinit var latestScreenshot: LatestScreenshot

    private lateinit var serviceHandler: Handler
    private lateinit var serviceLooper: Looper

    private var currentState = ServiceState.START
    private var command: String? = null
    private var toast: Toast? = null

    override fun onCreate() {
        application.component().inject(this)

        val handlerThread = HandlerThread(
                LensEmblemService::class.java.simpleName,
                Process.THREAD_PRIORITY_BACKGROUND)
        handlerThread.start()

        serviceLooper = handlerThread.looper
        serviceHandler = object: Handler(serviceLooper) {
            @Synchronized override fun handleMessage(msg: Message?) {
                if (command == ACTION_START && currentState != ServiceState.START) {
                    return
                }

                val state = ServiceState.values()[msg?.arg2 ?: 0]
                when (state) {
                    ServiceState.START -> {
                        makeToast(getString(R.string.service_started))
                        currentState = ServiceState.READY
                    }
                    ServiceState.READY -> {
                        currentState = ServiceState.PROCESSING
                        processAction()
                    }
                    else -> {
                        makeToast(getString(R.string.service_processing))
                    }
                }
            }
        }

        startForeground(SERVICE_ID, createNotification())
    }

    private fun processAction() {
        when(command) {
            ACTION_SCREENSHOT -> { takeScreenshot() }
            ACTION_STOP -> { stopSelf() }
            else -> {
                takeScreenshotAndProcessHero()
            }
        }

        currentState = ServiceState.READY
    }

    private fun takeScreenshot() {
        sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        makeToast(getString(R.string.service_taking_screenshot))
        pause(TIME_BEFORE_SCREENSHOT_MILLIS)

        screenshotHelper.takeScreenshot {
            try {
                screenshotTaken(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun takeScreenshotAndProcessHero() {
        makeToast(getString(R.string.service_taking_screenshot))
        pause(TIME_BEFORE_SCREENSHOT_MILLIS)

        screenshotHelper.takeScreenshot {
            try {
                makeToast(getString(R.string.service_processing))
                screenshotTaken(it)
                processHero(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun screenshotTaken(bitmap: Bitmap) {
        latestScreenshot.lastScreenshot = bitmap
    }

    private fun processHero(bitmap: Bitmap) {
        val hero = heroProcessor.processHeroProfile(bitmap)
        val heroFromDb = heroesRepo.getHero(hero.title, hero.name).blockingGet()

        val capturedStats = hero.stats?.first()
        val level = capturedStats?.level
        val sourceStats = heroFromDb?.stats?.find {
            it.equipped && it.level == level && ivProcessor.statsAdequatelyMatch(it, capturedStats)
        }

        if (capturedStats != null && sourceStats != null) {
            val baneBoon = ivProcessor.calculateIVs(sourceStats, capturedStats)

            makeToast(getString(R.string.hero_boon_bane,
                    heroFromDb.name,
                    heroFromDb.title,
                    baneBoon.first.name,
                    baneBoon.second.name), Toast.LENGTH_LONG)
        } else {
            makeToast(getString(R.string.error_could_not_find_hero))
        }
    }

    private fun pause(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun makeToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Handler(Looper.getMainLooper()).post {
            toast?.cancel()
            toast = Toast.makeText(applicationContext, msg, duration)
            toast?.show()
        }
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper.createChannelIfNecessary()
        }
        return notificationHelper.createNotification(
                getString(R.string.service_notification_title),
                getString(R.string.service_notification_message),
                NotificationAction.StopServiceAction(application),
                NotificationAction.ScreenshotAction(application))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val msg = serviceHandler.obtainMessage()
        msg.arg1 = startId
        msg.arg2 = currentState.ordinal

        command = intent?.action

        serviceHandler.sendMessage(msg)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LensEmblemService::class.java.simpleName, "Stopping Lens Emblem service.")
    }

    companion object {
        const val ACTION_STOP = "STOP"
        const val ACTION_SCREENSHOT = "SCREENSHOT"
        const val ACTION_START = "START"

        private const val TIME_BEFORE_SCREENSHOT_MILLIS = 3500L
        private const val SERVICE_ID = 1000

        fun start(context: Context): Intent {
            return Intent(context, LensEmblemService::class.java).apply {
                action = ACTION_START
            }
        }
    }
}