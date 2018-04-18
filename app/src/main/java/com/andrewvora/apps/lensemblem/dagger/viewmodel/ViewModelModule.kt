package com.andrewvora.apps.lensemblem.dagger.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.andrewvora.apps.lensemblem.boundspicker.BoundsPickerViewModel
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

    // view model factories
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}