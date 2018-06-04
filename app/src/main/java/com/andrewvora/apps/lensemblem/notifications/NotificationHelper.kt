package com.andrewvora.apps.lensemblem.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.main.MainActivity
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
                .setPriority(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                    NotificationManagerCompat.IMPORTANCE_HIGH
                } else {
                    NotificationCompat.PRIORITY_HIGH
                })
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)

        actions.forEach {
            val actionStr = app.getString(it.stringResId)
            builder = builder.addAction(it.drawableResId, actionStr, it.getPendingIntent())
        }

        return builder.build()
    }

    fun showAppNotification(notification: Notification) {
        val notificationManager = NotificationManagerCompat.from(app)
        notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification)
    }

    fun createFoundHeroNotification(heroId: Long, heroTitle: String, heroName: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            createChannelIfNecessary(DEFAULT_NOTIFICATION_CHANNEL_ID)
        }

        val title = app.getString(R.string.found_hero_notification_title)
        val message = app.getString(R.string.found_hero_notification_message, "\'$heroTitle - $heroName\'")

        val launchAppIntent = MainActivity.goToHeroDetailsActivity(app, heroId)
        val pendingIntent = PendingIntent.getActivity(app, 0, launchAppIntent, 0)
        return NotificationCompat.Builder(app, DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                    NotificationManagerCompat.IMPORTANCE_MAX
                } else {
                    NotificationCompat.PRIORITY_MAX
                })
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannelIfNecessary(channelId: String = SERVICE_CHANNEL_ID) {
        val channelAlreadyCreated = notificationManager.getNotificationChannel(channelId) != null

        if (channelAlreadyCreated.not()) {
            val channelName = app.getString(R.string.service_notification_channel_name)
            val channelDescription = app.getString(R.string.service_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val SERVICE_CHANNEL_ID = "Lens Emblem Service"
        private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "Lens Emblem App"

        private const val DEFAULT_NOTIFICATION_ID = 100
    }
}