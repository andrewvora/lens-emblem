package com.andrewvora.apps.lensemblem

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.widget.Toast
import com.andrewvora.apps.lensemblem.boundspicker.BoundingConfig
import com.andrewvora.apps.lensemblem.capturehistory.LatestScreenshot
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import com.andrewvora.apps.lensemblem.notifications.NotificationAction
import com.andrewvora.apps.lensemblem.notifications.NotificationHelper
import com.andrewvora.apps.lensemblem.ocr.OCRHeroProcessor
import com.andrewvora.apps.lensemblem.repos.BoundsRepo
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.andrewvora.apps.lensemblem.statprocessing.IVProcessor
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Background service that takes screenshots and processes them.
 * Created on 2/26/2018.
 * @author Andrew Vorakrajangthiti
 */
class LensEmblemService : Service() {

    enum class ServiceAction(val action: String) {
        START("START"),
        STOP("STOP"),
        PROCESS("PROCESS HERO"),
        SCREENSHOT_ONLY("SCREENSHOT");

        companion object {
            fun fromAction(str: String?): ServiceAction? {
                return ServiceAction.values().find {
                    it.action == str
                }
            }
        }
    }

    @Inject lateinit var screenshotHelper: ScreenshotHelper
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var heroProcessor: OCRHeroProcessor
    @Inject lateinit var boundsRepo: BoundsRepo
    @Inject lateinit var ivProcessor: IVProcessor
    @Inject lateinit var heroesRepo: HeroesRepo
    @Inject lateinit var latestScreenshot: LatestScreenshot

    private val disposables = CompositeDisposable()

    private lateinit var serviceHandler: Handler
    private lateinit var serviceLooper: Looper

    private var running = false
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
            @Synchronized
            override fun handleMessage(msg: Message?) {
                // do not process if:
                // - processing
                // - trying to start when already started
                val requestedAction = ServiceAction.fromAction(command)
                when {
                    running -> {
                        return
                    }
                }

                // update flag to start processing
                running = true

                // determine what to do
                runCommandSafely(requestedAction)

                // allow other messages to come through after 1000 ms
                // this is debounce duplicate events within the same window
                disposables.add(Completable.timer(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            running = false
                        })
            }
        }

        startForeground(SERVICE_ID, createNotification())
    }

    private fun runCommandSafely(action: ServiceAction?) {
        try {
            runCommand(action)
        } catch (e: Exception) {
            e.printStackTrace()

            // do not allow service to get locked
            running = false
        }
    }

    private fun runCommand(action: ServiceAction?) {
        when (action) {
            LensEmblemService.ServiceAction.START -> {
                setBoundingConfig()
                makeToast(getString(R.string.service_started))
            }
            LensEmblemService.ServiceAction.STOP -> {
                if (!disposables.isDisposed) {
                    disposables.dispose()
                }
                stopSelf()
            }
            LensEmblemService.ServiceAction.PROCESS -> {
                takeScreenshotAndProcessHero()
            }
            LensEmblemService.ServiceAction.SCREENSHOT_ONLY -> {
                takeScreenshot()
            }
        }
    }

    private fun setBoundingConfig() {
        val boundsToUse = BoundingConfig.CustomBoundingConfig(boundsRepo.getBounds().blockingGet())
        heroProcessor.setBounds(boundsToUse)
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
                screenshotTaken(it)
                processHero(it)
            } catch (e: Exception) {
                makeToast(getString(R.string.could_not_parse))
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
            it.level == level && ivProcessor.statsAdequatelyMatch(it, capturedStats)
        }

        if (capturedStats != null && sourceStats != null) {
            val baneBoon = ivProcessor.calculateIVs(sourceStats, capturedStats)

            makeToast(getString(R.string.hero_boon_bane,
                    heroFromDb.name,
                    heroFromDb.title,
                    baneBoon.first.name,
                    baneBoon.second.name), Toast.LENGTH_LONG)
        } else {
            if (heroFromDb != null) {
                val notification = notificationHelper.createFoundHeroNotification(
                        heroId = heroFromDb.id.toLong(),
                        heroTitle = heroFromDb.title,
                        heroName = heroFromDb.name
                )
                notificationHelper.showAppNotification(notification)

            }
            makeToast(getString(R.string.error_could_not_find_hero, hero.title, hero.name))
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
        return notificationHelper.createProcessHeroNotification(
                NotificationAction.StopServiceAction(application),
                NotificationAction.ScreenshotAction(application)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val msg = serviceHandler.obtainMessage()
        msg.arg1 = startId

        command = intent?.action

        if (!running) {
            serviceHandler.removeMessages(msg.what)
            serviceHandler.sendMessage(msg)
        }

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
        private const val TIME_BEFORE_SCREENSHOT_MILLIS = 3500L
        private const val SERVICE_ID = 1000

        fun start(context: Context): Intent {
            return Intent(context, LensEmblemService::class.java).apply {
                action = ServiceAction.START.action
            }
        }
    }
}