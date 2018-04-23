package com.andrewvora.apps.lensemblem.repos

import com.andrewvora.apps.lensemblem.models.AppMessage
import com.andrewvora.apps.lensemblem.repos.local.NotificationsLocalDataSource
import com.andrewvora.apps.lensemblem.repos.remote.NotificationsRemoteDataSource
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 3/4/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class NotificationsRepo
@Inject
constructor(private val remoteSource: NotificationsRemoteDataSource,
            private val localSource: NotificationsLocalDataSource) {

    fun fetchNotifications(): Single<List<AppMessage>> {
        return Single.defer {
            var notifications = remoteSource.getNotifications().blockingGet()
            if (notifications.isEmpty()) {
                notifications = localSource.loadNotificationsFromResource().blockingGet()
            }
            if (notifications.isNotEmpty()) {
                saveNotifications(notifications)
            }
            return@defer Single.just(notifications)
        }
    }

    fun saveNotifications(messages: List<AppMessage>) {
        localSource.deleteAllNotifications()
        localSource.saveNotifications(messages)
    }

    fun getNotifications(): Single<List<AppMessage>> {
        return Single.defer {
            val notifications = localSource.getNotifications().blockingGet()
            return@defer Single.just(notifications)
        }
    }

    fun getLatestNotification(): Single<AppMessage> {
        return Single.defer {
            val lastNotification = localSource.getMostRecentNotification().blockingGet()
            return@defer Single.just(lastNotification)
        }
    }
}