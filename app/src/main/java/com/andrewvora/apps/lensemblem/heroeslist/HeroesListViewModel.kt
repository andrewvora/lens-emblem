package com.andrewvora.apps.lensemblem.heroeslist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.andrewvora.apps.lensemblem.rxjava.useStandardObserveSubscribe
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created on 4/24/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroesListViewModel
@Inject
constructor(private val heroesRepo: HeroesRepo): ViewModel() {

    private val heroes = MutableLiveData<List<Hero>>()
    private val error = MutableLiveData<Throwable>()
    private val disposables = CompositeDisposable()

    fun getHeroes(): LiveData<List<Hero>> {
        return heroes
    }

    fun getError(): LiveData<Throwable> {
        return error
    }

    fun loadHeroes() {
        disposables.add(heroesRepo.getHeroes()
                .useStandardObserveSubscribe()
                .subscribe({
                    heroes.value = it
                }, {
                    error.value = it
                }))
    }

    fun refreshHeroes() {
        disposables.add(heroesRepo.fetchHeroes()
                .useStandardObserveSubscribe()
                .subscribe({
                    loadHeroes()
                }, {
                    error.value = it
                }))
    }

    override fun onCleared() {
        if (disposables.isDisposed.not()) {
            disposables.dispose()
        }
    }
}