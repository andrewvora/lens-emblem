package com.andrewvora.apps.lensemblem.repos.remote

import com.andrewvora.apps.lensemblem.dagger.ConstantsModule.Companion.HERO_STATS_URL
import com.andrewvora.apps.lensemblem.models.Hero
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class HeroesRemoteDataSource
@Inject constructor(private val httpClient: OkHttpClient,
                    private val gson: Gson,
                    @Named(HERO_STATS_URL) private val sourceUrl: String) {

    fun getHeroData(): Single<List<Hero>> {
        return Single.defer {
            val request = getRequest()
            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val heroes = gson.fromJson<List<Hero>>(
                        response.body()?.charStream(),
                        object: TypeToken<List<Hero>>() {}.type)

                return@defer Single.just(heroes)
            } else {
                throw Exception("Heroes request failed")
            }
        }
    }

    private fun getRequest(): Request {
        return Request.Builder()
                .url(sourceUrl)
                .build()
    }
}