package com.andrewvora.apps.lensemblem.repos.remote

import android.app.Application
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.Stats
import io.reactivex.Single
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class HeroesRemoteDataSource
@Inject constructor(private val application: Application,
                    private val httpClient: OkHttpClient) {

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