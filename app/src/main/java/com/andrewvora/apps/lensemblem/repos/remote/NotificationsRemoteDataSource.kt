package com.andrewvora.apps.lensemblem.repos.remote

import com.andrewvora.apps.lensemblem.models.NotificationInfo
import io.reactivex.Single
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 3/4/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class NotificationsRemoteDataSource
@Inject constructor(httpClient: OkHttpClient) {

    fun getNotifications(): Single<List<NotificationInfo>> {
        return Single.defer {
            Single.just<List<NotificationInfo>>(emptyList())
        }
    }
}