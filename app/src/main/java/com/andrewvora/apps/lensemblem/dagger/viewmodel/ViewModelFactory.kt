package com.andrewvora.apps.lensemblem.dagger.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Created on 4/14/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class ViewModelFactory
@Inject
constructor(
        private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]

        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }

        if (creator == null) {
            throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }

         try {
             return creator.get() as T
         } catch (e: Exception) {
             throw RuntimeException("Could not cast ${creator.get()} to $modelClass")
         }
    }
}