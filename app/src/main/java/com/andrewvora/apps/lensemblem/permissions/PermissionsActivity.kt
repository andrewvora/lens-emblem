package com.andrewvora.apps.lensemblem.permissions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper.Companion.REQUEST_CODE_SCREENSHOT_PERMISSION
import javax.inject.Inject


class PermissionsActivity : AppCompatActivity() {

    @Inject lateinit var screenshotHelper: ScreenshotHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        application.component().inject(this)

        getScreenCapturePermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, LensEmblemService::class.java))
    }

    private fun getScreenCapturePermission() {
        val permissionIntent = screenshotHelper.getPermissionIntent()
        startActivityForResult(permissionIntent, REQUEST_CODE_SCREENSHOT_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCREENSHOT_PERMISSION && resultCode == Activity.RESULT_OK) {
            startService(Intent(application, LensEmblemService::class.java))
            screenshotHelper.setPermissionResult(resultCode, data)
        }
    }
}
