package com.andrewvora.apps.lensemblem

import android.app.Application
import com.andrewvora.apps.lensemblem.dagger.AppComponent
import com.andrewvora.apps.lensemblem.dagger.AppModule
import com.andrewvora.apps.lensemblem.dagger.DaggerAppComponent
import com.andrewvora.apps.lensemblem.logging.CrashlyticsTree
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * Created on 2/27/2018.
 * @author Andrew Vorakrajangthiti
 */
class LensEmblemApp : Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        initFabric()
        initLogging()
    }

    private fun initFabric() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
    }

    private fun initLogging() {
        Timber.plant(CrashlyticsTree())
    }
}