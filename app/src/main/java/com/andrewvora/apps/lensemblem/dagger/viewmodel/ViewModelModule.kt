package com.andrewvora.apps.lensemblem.dagger.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.andrewvora.apps.lensemblem.boundspicker.BoundsPickerViewModel
import com.andrewvora.apps.lensemblem.herodetails.HeroDetailsViewModel
import com.andrewvora.apps.lensemblem.heroeslist.HeroesListViewModel
import com.andrewvora.apps.lensemblem.main.MainViewModel
import com.andrewvora.apps.lensemblem.tutorial.TutorialViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created on 4/14/2018.
 * @author Andrew Vorakrajangthiti
 */
@Module
abstract class ViewModelModule {
    // view models
    @Binds @IntoMap @ViewModelKey(BoundsPickerViewModel::class)
    internal abstract fun bindBoundsPickerViewModel(viewModel: BoundsPickerViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(HeroesListViewModel::class)
    internal abstract fun bindHeroesListViewModel(viewModel: HeroesListViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(HeroDetailsViewModel::class)
    internal abstract fun bindHeroDetailsViewModel(viewModel: HeroDetailsViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(TutorialViewModel::class)
    internal abstract fun bindTutorialViewModel(viewModel: TutorialViewModel): ViewModel

    // view model factories
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}