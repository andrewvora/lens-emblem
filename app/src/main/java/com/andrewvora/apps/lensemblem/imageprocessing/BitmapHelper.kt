package com.andrewvora.apps.lensemblem.imageprocessing

import android.graphics.Bitmap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 2/28/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class BitmapHelper
@Inject constructor() {
    fun getCharacterTitleBitmap(srcBitmap: Bitmap): Bitmap {
        val x = srcBitmap.width * 0.1
        val y = srcBitmap.height * 0.425
        val width = srcBitmap.width * 0.5
        val height = srcBitmap.height * 0.07
        return Bitmap.createBitmap(
                srcBitmap,
                x.toInt(),
                y.toInt(),
                width.toInt(),
                height.toInt())
    }

    fun getCharacterNameBitmap(srcBitmap: Bitmap): Bitmap {
        val x = srcBitmap.width * 0.1
        val y = srcBitmap.height * 0.48
        val width = srcBitmap.width * 0.45
        val height = srcBitmap.height * 0.07
        return Bitmap.createBitmap(
                srcBitmap,
                x.toInt(),
                y.toInt(),
                width.toInt(),
                height.toInt())
    }

    fun getCharacterLevelBitmap(srcBitmap: Bitmap): Bitmap {
        val x = srcBitmap.width * 0.1
        val y = srcBitmap.height * 0.57
        val width = srcBitmap.width * 0.3
        val height = srcBitmap.height * 0.05
        return Bitmap.createBitmap(
                srcBitmap,
                x.toInt(),
                y.toInt(),
                width.toInt(),
                height.toInt())
    }

    fun getCharacterStatsBitmap(srcBitmap: Bitmap): Bitmap {
        val x = srcBitmap.width * 0.1
        val y = srcBitmap.height * 0.62
        val width = srcBitmap.width * 0.35
        val height = srcBitmap.height * 0.275
        return Bitmap.createBitmap(
                srcBitmap,
                x.toInt(),
                y.toInt(),
                width.toInt(),
                height.toInt())
    }
}