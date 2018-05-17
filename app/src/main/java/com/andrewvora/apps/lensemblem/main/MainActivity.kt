package com.andrewvora.apps.lensemblem.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            stopService(LensEmblemService.start(application))
        }
    }
}