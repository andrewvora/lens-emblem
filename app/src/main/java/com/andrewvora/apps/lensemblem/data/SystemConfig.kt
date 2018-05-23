package com.andrewvora.apps.lensemblem.data

import android.app.Application
import android.text.format.DateFormat
import dagger.Reusable
import javax.inject.Inject

/**
 * Created on 5/23/2018.
 * @author Andrew Vorakrajangthiti
 */
@Reusable
class SystemConfig
@Inject
constructor(private val app: Application) {
    fun use24HourTime(): Boolean {
        return DateFormat.is24HourFormat(app)
    }
}