package com.andrewvora.apps.lensemblem.database.converters

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class RoomDateConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: Long?): Date? {
            return if (value == null) null else Date(value)
        }

        @TypeConverter
        @JvmStatic
        fun toTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
}