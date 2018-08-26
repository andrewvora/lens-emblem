package com.andrewvora.apps.lensemblem.alerts

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import dagger.Reusable
import javax.inject.Inject

/**
 * Created on 8/26/2018.
 * @author Andrew Vorakrajangthiti
 */
@Reusable
class LensEmblemAlerts
@Inject
constructor(private val app: Application) {

    fun toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Handler(Looper.getMainLooper()).run {
            Toast.makeText(app, msg, duration).show()
        }
    }
}