package com.andrewvora.apps.lensemblem.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 4/20/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class LensEmblemPreferences
@Inject
constructor(private val sharedPreferences: SharedPreferences) {

    fun hasLoadedDefaultData(): Boolean {
        return sharedPreferences.getBoolean(HAS_LOADED_DEFAULT_DATA, false)
    }

    fun setLoadedDefaultData(value: Boolean) {
        sharedPreferences.edit().putBoolean(HAS_LOADED_DEFAULT_DATA, value).apply()
    }

    fun lastCheckedNotificationTime(): Long {
        return sharedPreferences.getLong(LAST_CHECKED_NOTIFICATION_TIME, 0)
    }

    fun setLastCheckedNotificationTime(value: Long) {
        sharedPreferences.edit().putLong(LAST_CHECKED_NOTIFICATION_TIME, value).apply()
    }

    companion object {
        const val HAS_LOADED_DEFAULT_DATA = "loadedDefaultData"
        const val LAST_CHECKED_NOTIFICATION_TIME = "lastCheckedNotificationTime"
    }
}