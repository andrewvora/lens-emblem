package com.andrewvora.apps.lensemblem.dagger

import android.app.Application
import com.andrewvora.apps.lensemblem.LensEmblemApp
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.MainActivity
import com.andrewvora.apps.lensemblem.boundspicker.BoundsPickerFragment
import com.andrewvora.apps.lensemblem.dagger.viewmodel.ViewModelModule
import com.andrewvora.apps.lensemblem.notifications.NotificationFragment
import com.andrewvora.apps.lensemblem.permissions.PermissionsFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Component(modules = [AppModule::class, ConstantsModule::class, ViewModelModule::class])
@Singleton
interface AppComponent {
    fun inject(service: LensEmblemService)
    fun inject(fragment: PermissionsFragment)
    fun inject(fragment: BoundsPickerFragment)
    fun inject(fragment: NotificationFragment)
    fun inject(activity: MainActivity)
}

fun Application.component(): AppComponent {
    return (this as LensEmblemApp).component
}