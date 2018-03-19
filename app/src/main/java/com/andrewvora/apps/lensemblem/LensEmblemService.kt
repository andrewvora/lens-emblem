package com.andrewvora.apps.lensemblem

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import com.andrewvora.apps.lensemblem.notifications.NotificationHelper
import com.andrewvora.apps.lensemblem.ocr.OCRHeroProcessor
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.andrewvora.apps.lensemblem.statprocessing.IVProcessor
import javax.inject.Inject

/**
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
    @Inject lateinit var heroProcessor: OCRHeroProcessor
    @Inject lateinit var ivProcessor: IVProcessor
    @Inject lateinit var heroesRepo: HeroesRepo

    private lateinit var serviceHandler: Handler
    private lateinit var serviceLooper: Looper

    private var currentState = ServiceState.START
    private var stepCounter = 1

    override fun onCreate() {
        application.component().inject(this)

        val handlerThread = HandlerThread(
                LensEmblemService::class.java.simpleName,
                Process.THREAD_PRIORITY_BACKGROUND)
        handlerThread.start()
        serviceLooper = handlerThread.looper
        serviceHandler = object: Handler(serviceLooper) {
            @Synchronized override fun handleMessage(msg: Message?) {
                stepCounter++
                stepCounter %= (ServiceState.values().size + 1)
                val state = ServiceState.values()[msg?.arg2 ?: 0]
                when (state) {
                    ServiceState.START -> {
                        makeToast("$stepCounter. Service started!")
                        currentState = ServiceState.READY
                    }
                    ServiceState.READY -> {
                        currentState = ServiceState.PROCESSING
                        makeToast("$stepCounter. Capture in 5 seconds...")
                        pause(5000)

                        screenshotHelper.takeScreenshot {
                            val hero = heroProcessor.processHeroProfile(it)
                            val heroFromDb = heroesRepo.getHero(hero.title, hero.name).blockingGet()

                            val capturedStats = hero.stats?.first()
                            val level = capturedStats?.level
                            val sourceStats = heroFromDb?.stats?.find { it.equipped && it.level == level }

                            if (capturedStats != null && sourceStats != null) {
                                val baneBoon = ivProcessor.calculateIVs(sourceStats, capturedStats)
                                makeToast("${heroFromDb.title} ${heroFromDb.name}: " +
                                        "+${baneBoon.first.name}, -${baneBoon.second.name}")
                            } else {
                                makeToast(getString(R.string.error_could_not_find_hero))
                            }
                        }

                        currentState = ServiceState.READY
                    }
                    else -> {
                        makeToast("$stepCounter. Processing")
                    }
                }
            }
        }

        startForeground(SERVICE_ID, createNotification())
    }

    private fun pause(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
    // utility method for diagnosing bounding boxes
    private fun makeImageToast(bm: Bitmap) {
        Handler(Looper.getMainLooper()).post {
            val iv = ImageView(application).apply {
                setImageBitmap(bm)
            }
            Toast(application).apply {
                view = iv
                duration = Toast.LENGTH_SHORT
            }.show()
        }
    }
    */

    private fun makeToast(msg: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotification(): Notification {
        val notificationHelper = NotificationHelper(application)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper.createChannelIfNecessary()
        }
        return notificationHelper.createNotification("Yay", "I work")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val msg = serviceHandler.obtainMessage()
        msg.arg1 = startId
        msg.arg2 = currentState.ordinal

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
        private const val SERVICE_ID = 1000
    }
}