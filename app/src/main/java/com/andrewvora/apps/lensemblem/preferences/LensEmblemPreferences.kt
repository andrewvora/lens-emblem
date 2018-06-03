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

    fun lastHeroSync(): Long {
        return sharedPreferences.getLong(LAST_HERO_SYNC, 0)
    }

    fun setLastHeroSync(time: Long) {
        sharedPreferences.edit().putLong(LAST_HERO_SYNC, time).apply()
    }

    fun useDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(PREF_USE_DARK_THEME, false)
    }

    fun setDarkThemePreference(use: Boolean) {
        sharedPreferences.edit()
                .putBoolean(PREF_USE_DARK_THEME, use)
                .apply()
    }

    companion object {
        const val HAS_LOADED_DEFAULT_DATA = "loadedDefaultData"
        const val LAST_CHECKED_NOTIFICATION_TIME = "lastCheckedNotificationTime"
        const val LAST_HERO_SYNC = "lastTimeHeroesWereSynced"
        private const val PREF_USE_DARK_THEME = "useDarkTheme"
    }
}