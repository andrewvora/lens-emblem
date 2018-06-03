package com.andrewvora.apps.lensemblem.settings

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Created on 6/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class LensEmblemSettings
@Inject
constructor(private val sharedPreferences: SharedPreferences) {

    fun useDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(PREF_USE_DARK_THEME, true)
    }

    fun setDarkThemePreference(use: Boolean) {
        sharedPreferences.edit()
                .putBoolean(PREF_USE_DARK_THEME, use)
                .apply()
    }

    companion object {
        private const val PREF_USE_DARK_THEME = "useDarkTheme"
    }
}