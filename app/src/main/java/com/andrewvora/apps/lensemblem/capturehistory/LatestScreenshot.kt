package com.andrewvora.apps.lensemblem.capturehistory

import android.graphics.Bitmap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 4/9/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class LatestScreenshot
@Inject constructor() {
    var lastScreenshot: Bitmap? = null
}