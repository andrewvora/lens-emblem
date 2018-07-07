package com.andrewvora.apps.lensemblem.models

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

internal const val TABLE_HERO = "heroes"
internal const val COLUMN_TITLE = "title"
internal const val COLUMN_NAME = "name"
internal const val COLUMN_IMAGE_URL = "imageUrl"
internal const val COLUMN_WEAPON_TYPE = "weaponType"
internal const val COLUMN_WEAPON_TYPE_URL = "weaponTypeUrl"
internal const val COLUMN_MOVEMENT_TYPE = "movementType"
internal const val COLUMN_MOVEMENT_TYPE_URL = "movementTypeUrl"

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = TABLE_HERO)
data class Hero(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @ColumnInfo(name = COLUMN_IMAGE_URL)
        @SerializedName("imageUrl")
        var imageUrl: String? = null,

        @ColumnInfo(name = COLUMN_TITLE)
        @SerializedName("title")
        var title: String = "",

        @ColumnInfo(name = COLUMN_NAME)
        @SerializedName("name")
        var name: String = "",

        @ColumnInfo(name = COLUMN_WEAPON_TYPE)
        @SerializedName("weaponType")
        var weaponType: WeaponType? = WeaponType.UNKNOWN,
        @ColumnInfo(name = COLUMN_WEAPON_TYPE_URL)
        @SerializedName("weaponTypeUrl")
        var weaponTypeUrl: String? = null,

        @ColumnInfo(name = COLUMN_MOVEMENT_TYPE)
        @SerializedName("movementType")
        var movementType: MovementType? = MovementType.UNKNOWN,
        @ColumnInfo(name = COLUMN_MOVEMENT_TYPE_URL)
        @SerializedName("movementTypeUrl")
        var movementTypeUrl: String? = null,

        @Ignore
        @SerializedName("stats")
        var stats: List<Stats>? = null
): Parcelable