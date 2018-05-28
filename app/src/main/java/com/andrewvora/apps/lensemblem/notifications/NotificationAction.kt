package com.andrewvora.apps.lensemblem.notifications

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.andrewvora.apps.lensemblem.LensEmblemService
import com.andrewvora.apps.lensemblem.R

/**
 * Created on 4/19/2018.
 * @author Andrew Vorakrajangthiti
 */
sealed class NotificationAction(@DrawableRes val drawableResId: Int,
                                @StringRes val stringResId: Int) {

    abstract fun getPendingIntent(): PendingIntent

    // TODO update drawable resource
    class StopServiceAction(val app: Application) : NotificationAction(0, R.string.notification_stop_service) {
        override fun getPendingIntent(): PendingIntent {
            val intent = Intent(app, LensEmblemService::class.java).apply {
                action = LensEmblemService.ServiceAction.STOP.action
            }
            return PendingIntent.getService(app, 0, intent, 0)
        }
    }

    // TODO update drawable resource
    class ScreenshotAction(val app: Application) : NotificationAction(0, R.string.notification_screenshot) {
        override fun getPendingIntent(): PendingIntent {
            val intent = Intent(app, LensEmblemService::class.java).apply {
                action = LensEmblemService.ServiceAction.SCREENSHOT_ONLY.action
            }
            return PendingIntent.getService(app, 0, intent, 0)
        }
    }
}