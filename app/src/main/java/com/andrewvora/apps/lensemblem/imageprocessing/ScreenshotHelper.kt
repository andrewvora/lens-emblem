package com.andrewvora.apps.lensemblem.imageprocessing

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.view.WindowManager
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.alerts.LensEmblemAlerts
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 2/27/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
open class ScreenshotHelper
@Inject
constructor(private val app: Application,
            private val alerts: LensEmblemAlerts) {

    private val mediaProjectionManager = app.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private val windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val displayMetrics = DisplayMetrics().apply {
        windowManager.defaultDisplay.getRealMetrics(this)
    }
    private val screenWidth = displayMetrics.widthPixels
    private val screenHeight = displayMetrics.heightPixels
    private val screenDensity = app.resources.displayMetrics.densityDpi

    private var projection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private var resultCode = -1
    private var permissionIntent: Intent? = null

    fun setPermissionResult(resultCode: Int, permissionIntent: Intent?) {
        this.resultCode = resultCode
        this.permissionIntent = permissionIntent
    }

    private fun alertNoPermission() {
        alerts.toast(app.getString(R.string.missing_screenshot_permission))
    }

    fun hasPermission(): Boolean {
        return permissionIntent != null
    }

    fun getPermissionIntent(): Intent {
        return mediaProjectionManager.createScreenCaptureIntent()
    }

    fun takeScreenshot(callback: (Bitmap) -> Unit) {
        if (permissionIntent == null) {
            alertNoPermission()
            return
        }

        projection = projection ?: mediaProjectionManager.getMediaProjection(resultCode, permissionIntent)
        projection?.let {
            imageReader = imageReader ?: ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)

            if (virtualDisplay == null) {
                val flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR
                virtualDisplay = it.createVirtualDisplay(
                        "screenshot",
                        screenWidth,
                        screenHeight,
                        screenDensity,
                        flags,
                        imageReader?.surface,
                        null,
                        null)
            }

            captureScreenshot(imageReader, callback)
        }
    }

    fun cleanUp() {
        projection?.stop()
    }

    private fun captureScreenshot(reader: ImageReader?, callback: (Bitmap) -> Unit) {
        reader?.setOnImageAvailableListener({ _ ->
            reader.acquireLatestImage()?.let {
                val planes = it.planes
                val buffer = planes[0].buffer
                val pxStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val padding = rowStride - pxStride * screenWidth
                val bitmap = Bitmap.createBitmap(screenWidth + padding/pxStride, screenHeight, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)
                it.close()
                callback(bitmap)

                virtualDisplay?.release()
                virtualDisplay = null
            }
        }, null)
    }

    companion object {
        const val REQUEST_CODE_SCREENSHOT_PERMISSION = 1
    }
}