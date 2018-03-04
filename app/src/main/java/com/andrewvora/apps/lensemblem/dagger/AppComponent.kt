package com.andrewvora.apps.lensemblem.dagger

import android.app.Application
import com.andrewvora.apps.lensemblem.LensEmblemApp
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.permissions.PermissionsActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(service: LensEmblemService)
    fun inject(activity: PermissionsActivity)
}

fun Application.component(): AppComponent {
    return (this as LensEmblemApp).component
}