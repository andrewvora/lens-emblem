package com.andrewvora.apps.lensemblem.main

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.imageprocessing.ScreenshotHelper
import com.andrewvora.apps.lensemblem.preferences.LensEmblemPreferences
import javax.inject.Inject

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainActivity : AppCompatActivity() {

    @Inject lateinit var lensEmblemPreferences: LensEmblemPreferences
    @Inject lateinit var screenshotHelper: ScreenshotHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        application.component().inject(this)
        setTheme(if (lensEmblemPreferences.useDarkTheme()) {
            R.style.AppTheme_Embla
        } else {
            R.style.AppTheme_Askr
        })
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.extras?.let {
            val heroId = it.getLong(EXTRA_HERO_ID).toInt()

            if (heroId != 0) {
                intent.replaceExtras(Bundle()) // remove extras since it's now processed
                findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.hero_details_fragment, Bundle().apply {
                            putInt(EXTRA_HERO_ID, heroId)
                        })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            screenshotHelper.cleanUp()
            stopService(LensEmblemService.start(application))
        }
    }

    companion object {
        private const val EXTRA_HERO_ID = "heroId"

        fun goToHeroDetailsActivity(context: Context, heroId: Long): Intent {
            val intent = Intent(context, MainActivity::class.java)
            if (context is Application) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            return intent.apply {
                putExtra(EXTRA_HERO_ID, heroId)
            }
        }
    }
}