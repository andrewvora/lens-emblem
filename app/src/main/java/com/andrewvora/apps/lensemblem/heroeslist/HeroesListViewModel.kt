package com.andrewvora.apps.lensemblem.heroeslist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.andrewvora.apps.lensemblem.rxjava.useStandardObserveSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
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
    private val querySubject = PublishSubject.create<String>()

    init {
        disposables.add(querySubject.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchForHeroes(it)
                }, {
                    // do nothing
                }))
    }

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

    private fun searchForHeroes(query: String) {
        when {
            query.isNotBlank() -> {
                disposables.add(heroesRepo.getHeroes(query)
                        .useStandardObserveSubscribe()
                        .subscribe({
                            heroes.value = it
                        }, {
                            error.value = it
                        }))
            }
            else -> loadHeroes()
        }
    }

    fun findHeroesMatching(query: String?) {
        querySubject.onNext(query ?: "")
    }

    override fun onCleared() {
        if (disposables.isDisposed.not()) {
            disposables.dispose()
        }
    }
}