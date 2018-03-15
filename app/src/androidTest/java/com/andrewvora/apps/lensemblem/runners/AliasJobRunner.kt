package com.andrewvora.apps.lensemblem.runners

import android.app.Application
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.andrewvora.apps.lensemblem.data.FullScreenActivity
import com.andrewvora.apps.lensemblem.imageprocessing.BitmapHelper
import com.andrewvora.apps.lensemblem.ocr.OCRHelper
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Only runs when explicitly targeted.
 *
 * Created on 3/14/2018.
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4::class)
@Ignore
class AliasJobRunner {
    private lateinit var ocrHelper: OCRHelper
    private lateinit var bitmapHelper: BitmapHelper

    private var fullScreenActivityIntent: Intent? = null
    @Rule val activityTestRule = object: ActivityTestRule<FullScreenActivity>(
            FullScreenActivity::class.java,
            false,
            false) {
        override fun getActivityIntent(): Intent {
            return fullScreenActivityIntent!!
        }
    }

    @Before
    fun setUp() {
        ocrHelper = OCRHelper(InstrumentationRegistry.getContext().applicationContext as Application)
        bitmapHelper = BitmapHelper()
    }

    @Test
    fun getAliases() {

    }

    fun writeToJsonFile(json: String): Boolean {
        return true
    }
}