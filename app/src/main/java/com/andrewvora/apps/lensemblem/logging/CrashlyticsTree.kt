package com.andrewvora.apps.lensemblem.logging

import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (Crashlytics.getInstance() != null && t != null) {
            Crashlytics.logException(t)
        }
    }
}