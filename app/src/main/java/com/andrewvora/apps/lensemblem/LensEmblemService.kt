package com.andrewvora.apps.lensemblem

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.notifications.NotificationHelper
import com.andrewvora.apps.lensemblem.ocr.OCRHelper
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import javax.inject.Inject

/**
 * Created on 2/26/2018.
 * @author Andrew Vorakrajangthiti
 */
class LensEmblemService : Service() {

    @Inject lateinit var screenshotHelper: ScreenshotHelper
    @Inject lateinit var ocrHelper: OCRHelper

    private lateinit var serviceHandler: Handler
    private lateinit var serviceLooper: Looper

    override fun onCreate() {
        application.component().inject(this)

        val handlerThread = HandlerThread(
                LensEmblemService::class.java.simpleName,
                Process.THREAD_PRIORITY_BACKGROUND)
        handlerThread.start()
        serviceLooper = handlerThread.looper
        serviceHandler = object: Handler(serviceLooper) {
            override fun handleMessage(msg: Message?) {
                when (msg?.arg2) {
                    CMD_START -> makeToast("Service started!")
                    CMD_PROCESS -> {
                        makeToast("Capture in 3 seconds...")
                        pause(3000)

                        screenshotHelper.takeScreenshot {

                        }
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
        msg.arg2 = CMD_START

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

        private const val CMD_START = 1
        private const val CMD_PROCESS = 2
    }
}