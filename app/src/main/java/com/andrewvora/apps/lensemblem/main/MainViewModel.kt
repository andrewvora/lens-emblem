package com.andrewvora.apps.lensemblem.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.andrewvora.apps.lensemblem.models.AppMessage
import com.andrewvora.apps.lensemblem.preferences.LensEmblemPreferences
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created on 4/19/2018.
 * @author Andrew Vorakrajangthiti
 */
class MainViewModel
@Inject
constructor(private val heroesRepo: HeroesRepo,
            private val lensEmblemPrefs: LensEmblemPreferences) : ViewModel() {

    var currentState: State = State.DEFAULT
        set(value) {
            field = value
            state.value = value
        }

    private val state = MutableLiveData<State>()
    private val heroesLoaded = MutableLiveData<Boolean>()
    private val notifications = MutableLiveData<List<AppMessage>>()
    private val error = MutableLiveData<Throwable>()
    private val disposables = CompositeDisposable()
    private var isLoadingHeroes = false

    fun getHeroesLoaded(): LiveData<Boolean> {
        return heroesLoaded
    }

    fun getNotifications(): LiveData<List<AppMessage>> {
        return notifications
    }

    fun getError(): LiveData<Throwable> {
        return error
    }

    fun getState(): LiveData<State> {
        return state
    }

    fun heroesLoaded(): Boolean {
        return lensEmblemPrefs.hasLoadedDefaultData()
    }

    fun loadHeroes() {
        if (isLoadingHeroes.not()) {
            isLoadingHeroes = true
            disposables.add(heroesRepo.loadDefaultData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {
                        error.value = it
                        isLoadingHeroes = false
                    }
                    .doOnComplete {
                        lensEmblemPrefs.setLoadedDefaultData(true)
                        heroesLoaded.value = true
                    }
                    .subscribe())
        }
    }

    fun syncHeroData() {

    }

    override fun onCleared() {
        if (disposables.isDisposed.not()) {
            disposables.clear()
        }
    }

    enum class State {
        DEFAULT, HEROES_LOADED, PERMISSION_GRANTED, SERVICE_STARTED
    }
}