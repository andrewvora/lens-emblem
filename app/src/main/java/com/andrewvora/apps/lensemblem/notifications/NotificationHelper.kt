package com.andrewvora.apps.lensemblem.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R


/**
 * Created on 2/27/2018.
 * @author Andrew Vorakrajangthiti
 */
class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(title: String, msg: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelIfNecessary()
        }

        val notification = createNotification(title, msg)
        notificationManager.notify(PRIMARY_NOTIFICATION, notification)
    }

    fun createNotification(title: String, msg: String): Notification {
        val launchAppIntent = Intent(context, LensEmblemService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, launchAppIntent, 0)
        return NotificationCompat.Builder(context, SERVICE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannelIfNecessary() {
        val channelAlreadyCreated = notificationManager.getNotificationChannel(SERVICE_CHANNEL_ID) != null

        if (channelAlreadyCreated.not()) {
            val channelName = context.getString(R.string.service_notification_channel_name)
            val channelDescription = context.getString(R.string.service_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(SERVICE_CHANNEL_ID, channelName, importance).apply {
                description = channelDescription
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val SERVICE_CHANNEL_ID = "Lens Emblem Service"
        private const val PRIMARY_NOTIFICATION = 100
    }
}