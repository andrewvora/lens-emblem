package com.andrewvora.apps.lensemblem.permissions

/**
 * Created on 4/6/2018.
 * @author Andrew Vorakrajangthiti
 */
interface PermissionListener {
    fun onScreenCapturePermissionGranted()
    fun onScreenCapturePermissionDenied()
}