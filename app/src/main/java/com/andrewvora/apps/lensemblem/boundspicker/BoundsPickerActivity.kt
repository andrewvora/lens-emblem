package com.andrewvora.apps.lensemblem.boundspicker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andrewvora.apps.lensemblem.R

/**
 * Created on 4/6/2018.
 * @author Andrew Vorakrajangthiti
 */
class BoundsPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bounds_picker)
    }

    companion object {
        fun start(context: Context): Intent {
            return Intent(context, BoundsPickerActivity::class.java)
        }
    }
}