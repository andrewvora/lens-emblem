package com.andrewvora.apps.lensemblem.repos.remote

import com.andrewvora.apps.lensemblem.dagger.ConstantsModule.Companion.NOTIFICATIONS_URL
import com.andrewvora.apps.lensemblem.models.AppMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created on 3/4/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class NotificationsRemoteDataSource
@Inject constructor(private val httpClient: OkHttpClient,
                    private val gson: Gson,
                    @Named(NOTIFICATIONS_URL) private val url: String) {

    fun getNotifications(): Single<List<AppMessage>> {
        return Single.defer {
            val request = getRequest()
            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val messages = gson.fromJson<List<AppMessage>>(
                        response.body()?.charStream(),
                        object: TypeToken<List<AppMessage>>() {}.type)

                return@defer Single.just(messages)
            }
            throw Exception("Notification request failed")
        }
    }

    private fun getRequest(): Request {
        return Request.Builder()
                .url(url)
                .build()
    }
}