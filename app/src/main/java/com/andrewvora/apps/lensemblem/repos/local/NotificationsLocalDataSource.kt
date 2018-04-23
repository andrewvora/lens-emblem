package com.andrewvora.apps.lensemblem.repos.local

import android.app.Application
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.database.LensEmblemDatabase
import com.andrewvora.apps.lensemblem.models.AppMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created on 4/22/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class NotificationsLocalDataSource
@Inject
constructor(private val app: Application,
            private val database: LensEmblemDatabase,
            private val gson: Gson) {

    fun saveNotifications(messages: List<AppMessage>) {
        database.messageDao().insert(*messages.toTypedArray())
    }

    fun getNotifications(): Single<List<AppMessage>> {
        return Single.just(database.messageDao().getMessages())
    }

    fun getMostRecentNotification(): Single<AppMessage> {
        val lastNotification = database.messageDao().getLatestMessage()
        return if (lastNotification != null) {
            Single.just(lastNotification)
        } else {
            Single.never()
        }
    }

    fun loadNotificationsFromResource(): Single<List<AppMessage>> {
        val inputStream = app.resources.openRawResource(R.raw.notifications_v1)
        val json = inputStream.bufferedReader().use { it.readText() }
        val messages = gson.fromJson<List<AppMessage>>(json,
                object: TypeToken<List<AppMessage>>() {}.type)

        return Single.just(messages)
    }

    fun deleteAllNotifications() {
        database.messageDao().deleteAllMessages()
    }
}