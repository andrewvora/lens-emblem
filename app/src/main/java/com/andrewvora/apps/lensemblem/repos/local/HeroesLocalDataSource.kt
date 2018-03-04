package com.andrewvora.apps.lensemblem.repos.local

import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.Stats
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 3/4/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class HeroesLocalDataSource
@Inject constructor() {

    fun getHeroData(): Single<List<Hero>> {
        return Single.defer {
            Single.just<List<Hero>>(emptyList())
        }
    }

    fun getHeroStatsData(): Single<List<Stats>> {
        return Single.defer {
            Single.just<List<Stats>>(emptyList())
        }
    }
}