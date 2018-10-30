package com.andrewvora.apps.lensemblem.updater

import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.dagger.component
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.crashlytics.android.Crashlytics
import timber.log.Timber
import javax.inject.Inject

/**
 * Created on 8/25/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroUpdaterService : JobIntentService() {

    @Inject lateinit var heroesRepo: HeroesRepo
    @Inject lateinit var heroUpdaterSubject: HeroUpdater

    override fun onHandleWork(intent: Intent) {
        val loggingTag = getString(R.string.app_name)
        Log.i(loggingTag, "Starting hero updater service.")

        // load up dependencies
        application.component().inject(this)

        // update database and alert subscribers
        try {
            heroesRepo.fetchHeroes().blockingAwait()
            heroUpdaterSubject.subject.onNext(true)
        } catch (e: Exception) {
            Timber.e(e)
            Timber.i("$loggingTag: Failed to update database.")
        }
    }
}