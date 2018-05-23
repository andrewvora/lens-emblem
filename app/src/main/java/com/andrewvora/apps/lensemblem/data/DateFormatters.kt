package com.andrewvora.apps.lensemblem.data

import dagger.Reusable
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created on 5/22/2018.
 * @author Andrew Vorakrajangthiti
 */
private const val TIMESTAMP_FORMAT_12H = "dd MMM, yyyy h:mm a"
private const val TIMESTAMP_FORMAT_24H = "dd MMM, yyyy H:mm"

@Reusable
class TimestampFormatter
@Inject
constructor() {
    fun format(date: Date, use12H: Boolean): String {
        return SimpleDateFormat(if (use12H) {
            TIMESTAMP_FORMAT_12H
        } else {
            TIMESTAMP_FORMAT_24H
        }, Locale.getDefault()).format(date)
    }
}