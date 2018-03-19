package com.andrewvora.apps.lensemblem.imageprocessing

import android.graphics.*
import android.support.annotation.IntRange
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created on 2/28/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class BitmapHelper
@Inject constructor() {

    private lateinit var boundingConfig: BoundingConfig

    fun setBoundingConfig(boundingConfig: BoundingConfig) {
        this.boundingConfig = boundingConfig
    }

    fun getCharacterTitleBitmap(srcBitmap: Bitmap): Bitmap {
        val x = srcBitmap.width * boundingConfig.characterTitle().xMod
        val y = srcBitmap.height * boundingConfig.characterTitle().yMod
        val width = srcBitmap.width * boundingConfig.characterTitle().widthMod
        val height = srcBitmap.height * boundingConfig.characterTitle().heightMod
        return Bitmap.createBitmap(
                srcBitmap,
                x.toInt(),
                y.toInt(),
                width.toInt(),
                height.toInt())
    }

    fun getCharacterNameBitmap(srcBitmap: Bitmap): Bitmap {
        val x = srcBitmap.width * boundingConfig.characterName().xMod
        val y = srcBitmap.height * boundingConfig.characterName().yMod
        val width = srcBitmap.width * boundingConfig.characterName().widthMod
        val height = srcBitmap.height * boundingConfig.characterName().heightMod
        return Bitmap.createBitmap(
                srcBitmap,
                x.toInt(),
                y.toInt(),
                width.toInt(),
                height.toInt())
    }

    fun getCharacterLevelBitmap(srcBitmap: Bitmap): Bitmap {
        val x = srcBitmap.width * boundingConfig.characterLevel().xMod
        val y = srcBitmap.height * boundingConfig.characterLevel().yMod
        val width = srcBitmap.width * boundingConfig.characterLevel().widthMod
        val height = srcBitmap.height * boundingConfig.characterLevel().heightMod
        return Bitmap.createBitmap(
                srcBitmap,
                x.toInt(),
                y.toInt(),
                width.toInt(),
                height.toInt())
    }

    fun getCharacterStatsBitmap(srcBitmap: Bitmap): Bitmap {
        val x = srcBitmap.width * boundingConfig.characterStats().xMod
        val y = srcBitmap.height * boundingConfig.characterStats().yMod
        val width = srcBitmap.width * boundingConfig.characterStats().widthMod
        val height = srcBitmap.height * boundingConfig.characterStats().heightMod
        return Bitmap.createBitmap(
                srcBitmap,
                x.toInt(),
                y.toInt(),
                width.toInt(),
                height.toInt())
    }

    fun makeHighContrast(srcBitmap: Bitmap,
                         @IntRange(from = 0, to = 255)
                         threshold: Int = DEFAULT_GRAY_THRESHOLD): Bitmap
    {
        val resultBitmap = srcBitmap.copy(Bitmap.Config.ARGB_8888, true)
        var alpha: Int
        var red: Int
        var green: Int
        var blue: Int
        var pixel: Int

        // scan through all pixels
        for (x in 0 until srcBitmap.width) {
            for (y in 0 until srcBitmap.height) {
                // get pixel color
                pixel = srcBitmap.getPixel(x, y)
                alpha = Color.alpha(pixel)
                red = Color.red(pixel)
                green = Color.green(pixel)
                blue = Color.blue(pixel)
                var gray = (0.2989 * red + 0.5870 * green + 0.1140 * blue).toInt()

                // use 128 as threshold, above -> white, below -> black
                gray = if (gray > threshold) 255 else 0
                // set new pixel color to output bitmap
                resultBitmap.setPixel(x, y, Color.argb(alpha, gray, gray, gray))
            }
        }

        return resultBitmap
    }
}

private const val DEFAULT_GRAY_THRESHOLD = 190