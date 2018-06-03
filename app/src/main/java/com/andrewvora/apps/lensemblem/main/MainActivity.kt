package com.andrewvora.apps.lensemblem.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.preferences.LensEmblemPreferences
import javax.inject.Inject

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainActivity : AppCompatActivity() {

    @Inject lateinit var lensEmblemPreferences: LensEmblemPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        application.component().inject(this)
        setTheme(if (lensEmblemPreferences.useDarkTheme()) {
            R.style.AppTheme_Embla
        } else {
            R.style.AppTheme_Askr
        })
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