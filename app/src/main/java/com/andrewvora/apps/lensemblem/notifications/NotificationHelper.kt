package com.andrewvora.apps.lensemblem.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created on 2/27/2018.
 * @author Andrew Vorakrajangthiti
 */
@Singleton
class NotificationHelper
@Inject
constructor(private val app: Application) {

    private val notificationManager = app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createProcessHeroNotification(vararg actions: NotificationAction): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelIfNecessary()
        }

        val title = app.getString(R.string.service_notification_title)
        val message = app.getString(R.string.service_notification_message)
        val launchAppIntent = Intent(app, LensEmblemService::class.java).apply {
            this.action = LensEmblemService.ServiceAction.PROCESS.action
        }
        val pendingIntent = PendingIntent.getService(app, 0, launchAppIntent, 0)
        var builder = NotificationCompat.Builder(app, SERVICE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)

        actions.forEach {
            val actionStr = app.getString(it.stringResId)
            builder = builder.addAction(it.drawableResId, actionStr, it.getPendingIntent())
        }

        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannelIfNecessary() {
        val channelAlreadyCreated = notificationManager.getNotificationChannel(SERVICE_CHANNEL_ID) != null

        if (channelAlreadyCreated.not()) {
            val channelName = app.getString(R.string.service_notification_channel_name)
            val channelDescription = app.getString(R.string.service_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(SERVICE_CHANNEL_ID, channelName, importance).apply {
                description = channelDescription
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val SERVICE_CHANNEL_ID = "Lens Emblem Service"
    }
}