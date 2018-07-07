package com.andrewvora.apps.lensemblem.database.converters

import android.arch.persistence.room.TypeConverter
import com.andrewvora.apps.lensemblem.models.BoundsType
import com.andrewvora.apps.lensemblem.models.MovementType
import com.andrewvora.apps.lensemblem.models.WeaponType
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

class BoundsTypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromBoundsTypeOrdinal(value: Int): BoundsType {
            return BoundsType.values()[value]
        }

        @TypeConverter
        @JvmStatic
        fun toBoundsTypeOrdinal(value: BoundsType): Int {
            return value.ordinal
        }
    }
}

class WeaponTypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromWeaponTypeOrdinal(value: Int): WeaponType {
            return WeaponType.values()[value]
        }

        @TypeConverter
        @JvmStatic
        fun toWeaponTypeOrdinal(value: WeaponType?): Int {
            return (value ?: WeaponType.UNKNOWN).ordinal
        }
    }
}

class MovementTypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromMovementTypeOrdinal(value: Int): MovementType {
            return MovementType.values()[value]
        }

        @TypeConverter
        @JvmStatic
        fun toMovementTypeOrdinal(value: MovementType?): Int {
            return (value ?: MovementType.UNKNOWN).ordinal
        }
    }
}