package com.andrewvora.apps.lensemblem.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R
import com.andrewvora.apps.lensemblem.main.MainActivity
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.statprocessing.IVProcessor
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
    private var ivResultSummaryShown = false

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
                    NotificationManagerCompat.IMPORTANCE_MAX
                } else {
                    NotificationCompat.PRIORITY_MAX
                })
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)

        actions.forEach {
            val actionStr = app.getString(it.stringResId)
            builder = builder.addAction(it.drawableResId, actionStr, it.getPendingIntent())
        }

        return builder.build()
    }

    fun showAppNotification(notification: Notification, notificationId: Int = DEFAULT_NOTIFICATION_ID) {
        val notificationManager = NotificationManagerCompat.from(app)
        notificationManager.notify(notificationId, notification)
    }

    fun createMatchedHeroNotification(hero: Hero,
                                      baneBoon: Pair<IVProcessor.Stat, IVProcessor.Stat>,
                                      heroIcon: Bitmap?): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            createChannelIfNecessary(DEFAULT_NOTIFICATION_CHANNEL_ID)
        }

        val title = "${hero.name} - ${hero.title}"
        val message = "+${baneBoon.first.name}, -${baneBoon.second.name}"

        val launchAppIntent = MainActivity.goToHeroDetailsActivity(app, hero.id.toLong())
        val pendingIntent = PendingIntent.getActivity(app, 0, launchAppIntent, PendingIntent.FLAG_ONE_SHOT)
        return NotificationCompat.Builder(app, DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setGroup(IV_GROUP_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(heroIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(getDefaultPriority())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
    }

    fun createFoundHeroNotification(heroId: Long, heroTitle: String, heroName: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            createChannelIfNecessary(DEFAULT_NOTIFICATION_CHANNEL_ID)
        }

        val title = app.getString(R.string.found_hero_notification_title)
        val message = app.getString(R.string.found_hero_notification_message, "\'$heroTitle - $heroName\'")

        val launchAppIntent = MainActivity.goToHeroDetailsActivity(app, heroId)
        val pendingIntent = PendingIntent.getActivity(app, 0, launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(app, DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(getDefaultPriority())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .build()
    }

    private fun getDefaultPriority(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }
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

    fun showHeroIvResultsSummaryNotification() {
        if (ivResultSummaryShown) {
            return
        }
        val content = app.getString(R.string.iv_results_summary_short_description)
        val title = app.getString(R.string.iv_results_summary_title)
        val summaryNotification = NotificationCompat.Builder(app, DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setGroup(IV_GROUP_ID)
                .setGroupSummary(true)
                .setContentText(content)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notification)
                .setStyle(NotificationCompat.InboxStyle()
                        .setBigContentTitle(title)
                        .setSummaryText(content))
                .build()

        notificationManager.notify(IV_RESULTS_NOTIFICATION_ID, summaryNotification)
        ivResultSummaryShown = true
    }

    companion object {
        private const val SERVICE_CHANNEL_ID = "Lens Emblem Service"
        private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "Lens Emblem App"

        private const val DEFAULT_NOTIFICATION_ID = 100
        const val IV_RESULTS_NOTIFICATION_ID = 101
        private const val IV_GROUP_ID = "com.andrewvora.lensemblem.HERO_IV_RESULTS"
    }
}