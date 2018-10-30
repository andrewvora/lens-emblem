package com.andrewvora.apps.lensemblem.updater

import android.app.Application
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.andrewvora.apps.lensemblem.rxjava.useStandardObserveSubscribe
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Abstraction around the [HeroUpdaterService].
 * Exposes a PublishSubject and operations for affecting the service.
 *
 * @link PublishSubject
 *
 * Created on 8/25/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
open class HeroUpdater
@Inject
constructor(private val app: Application,
            private val heroesRepo: HeroesRepo) {

    val subject: PublishSubject<Any> = PublishSubject.create()

    open fun load() {
        val jobId = 100
        val intent = Intent()
        JobIntentService.enqueueWork(app, HeroUpdaterService::class.java, jobId, intent)
    }

    open fun loadLocal(): Disposable {
        return heroesRepo.loadDefaultData()
                .useStandardObserveSubscribe()
                .subscribe({
                    subject.onNext(true)
                }, {
                    Timber.d("${HeroUpdater::class.java.simpleName}: ${it.message}")
                    Timber.e(it)
                })
    }
}