package com.andrewvora.apps.lensemblem.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andrewvora.apps.lensemblem.R

/**
 * Created on 4/20/2018.
 * @author Andrew Vorakrajangthiti
 */
class NotificationsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
    }

    companion object {
        fun start(context: Context): Intent {
            return Intent(context, NotificationsActivity::class.java)
        }
    }
}