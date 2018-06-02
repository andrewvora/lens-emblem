package com.andrewvora.apps.lensemblem

import android.app.Application
import com.andrewvora.apps.lensemblem.dagger.AppComponent
import com.andrewvora.apps.lensemblem.dagger.AppModule
import com.andrewvora.apps.lensemblem.dagger.DaggerAppComponent

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
    }
}