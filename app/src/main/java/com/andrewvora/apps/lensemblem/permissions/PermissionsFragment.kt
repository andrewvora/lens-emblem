package com.andrewvora.apps.lensemblem.permissions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper.Companion.REQUEST_CODE_SCREENSHOT_PERMISSION
import kotlinx.android.synthetic.main.fragment_permissions.*
import javax.inject.Inject

/**
 * Handles requesting permissions required by the Lens Emblem app.
 * Currently, requests the screen sharing permission.
 */
class PermissionsFragment : Fragment() {

    @Inject lateinit var screenshotHelper: ScreenshotHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.application?.component()?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screen_capture_permission_button.setOnClickListener {
            getScreenCapturePermission()
        }
    }

    private fun getScreenCapturePermission() {
        val permissionIntent = screenshotHelper.getPermissionIntent()
        startActivityForResult(permissionIntent, REQUEST_CODE_SCREENSHOT_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCREENSHOT_PERMISSION && resultCode == Activity.RESULT_OK) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    alertPermissionGranted(true)
                    screenshotHelper.setPermissionResult(resultCode, data)
                }
                else -> {
                    alertPermissionGranted(false)
                }
            }
        }
    }

    private fun alertPermissionGranted(granted: Boolean) {
        if (parentFragment is PermissionListener) {
            val listener = parentFragment as PermissionListener
            if (granted) {
                listener.onScreenCapturePermissionGranted()
            } else {
                listener.onScreenCapturePermissionDenied()
            }
        }
    }


}
