package com.andrewvora.apps.lensemblem.models

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Bounding box parameters. Used to create region for bitmap.
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = TABLE_BOUNDS)
data class Bounds(
        @PrimaryKey(autoGenerate = true) var id: Int,
        @ColumnInfo(name = COLUMN_X_MOD) var xMod: Double = 0.0,
        @ColumnInfo(name = COLUMN_Y_MOD) var yMod: Double = 0.0,
        @ColumnInfo(name = COLUMN_WIDTH_MOD) var widthMod: Double = 0.0,
        @ColumnInfo(name = COLUMN_HEIGHT_MOD) var heightMod: Double = 0.0
): Parcelable

internal const val TABLE_BOUNDS = "bounds"
internal const val COLUMN_X_MOD = "x_mod"
internal const val COLUMN_Y_MOD = "y_mod"
internal const val COLUMN_WIDTH_MOD = "width_mod"
internal const val COLUMN_HEIGHT_MOD = "height_mod"