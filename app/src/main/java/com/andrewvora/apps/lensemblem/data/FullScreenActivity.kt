package com.andrewvora.apps.lensemblem.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andrewvora.apps.lensemblem.R

/**
 * Used to display images full screen.
 * Mainly used by
 * @link AliasJobRunner.kt
 * to display screenshots and gather aliases.
 *
 * Created on 3/14/2018.
 * @author Andrew Vorakrajangthiti
 */
class FullScreenActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)
    }

    companion object {
        private const val FILE_PATH = "FilePath"

        fun getIntent(context: Context, assetsFile: String): Intent {
            return Intent(context, FullScreenActivity::class.java)
                    .putExtra(FILE_PATH, assetsFile)
        }
    }
}