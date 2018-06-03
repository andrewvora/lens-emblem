package com.andrewvora.apps.lensemblem.herodetails

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.Stats
import com.andrewvora.apps.lensemblem.repos.HeroesRepo
import com.andrewvora.apps.lensemblem.rxjava.useStandardObserveSubscribe
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created on 5/30/2018.
 * @author Andrew Vorakrajangthiti
 */
class HeroDetailsViewModel
@Inject
constructor(private val app: Application,
            private val heroesRepo: HeroesRepo): ViewModel() {

    private val hero = MutableLiveData<Hero>()
    private val statsToDisplay = MutableLiveData<Pair<Stats?, Stats?>>()
    private val errorMessage = MutableLiveData<String>()
    private val disposables = CompositeDisposable()

    fun getHeroLiveData(): LiveData<Hero> {
        return hero
    }

    fun getStatsToDisplayLiveData(): LiveData<Pair<Stats?, Stats?>> {
        return statsToDisplay
    }

    fun getErrorMessageLiveData(): LiveData<String> {
        return errorMessage
    }

    fun loadStats(rarity: Int, equipped: Boolean) {
        hero.value?.stats?.filter {
            it.equipped == equipped && it.rarity == rarity
        }?.let {
            val lvl1Stats = it.find { it.level == 1 }
            val lvl40Stats = it.find { it.level == 40 }

            statsToDisplay.value = lvl1Stats to lvl40Stats
        }
    }

    fun loadHero(id: Long) {
        disposables.add(heroesRepo.getHero(id)
                .useStandardObserveSubscribe()
                .subscribe({
                    hero.value = it
                }, {
                    errorMessage.value = app.getString(R.string.network_error)
                }))
    }

    fun getHeroMinRarity(): Int {
        return hero.value?.stats?.minBy { it.rarity }?.rarity ?: 0
    }

    override fun onCleared() {
        if (disposables.isDisposed.not()) {
            disposables.dispose()
        }
        super.onCleared()
    }
}