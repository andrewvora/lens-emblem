package com.andrewvora.apps.lensemblem.dagger

import android.app.Application
import com.andrewvora.apps.lensemblem.LensEmblemApp
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.main.MainActivity
import com.andrewvora.apps.lensemblem.boundspicker.BoundsPickerActivity
import com.andrewvora.apps.lensemblem.dagger.viewmodel.ViewModelModule
import com.andrewvora.apps.lensemblem.herodetails.HeroDetailsFragment
import com.andrewvora.apps.lensemblem.heroeslist.HeroesListFragment
import com.andrewvora.apps.lensemblem.main.MainFragment
import com.andrewvora.apps.lensemblem.permissions.PermissionsFragment
import com.andrewvora.apps.lensemblem.tutorial.TutorialFragment
import com.andrewvora.apps.lensemblem.updater.HeroUpdaterService
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
    fun inject(service: HeroUpdaterService)

    fun inject(fragment: PermissionsFragment)
    fun inject(activity: BoundsPickerActivity)
    fun inject(fragment: HeroesListFragment)
    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)
    fun inject(fragment: HeroDetailsFragment)
    fun inject(fragment: TutorialFragment)
}

fun Application.component(): AppComponent {
    return (this as LensEmblemApp).component
}