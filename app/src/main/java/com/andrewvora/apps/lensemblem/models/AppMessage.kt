package com.andrewvora.apps.lensemblem.models

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * App notification object.
 * Created on 3/4/2018.
 * @author Andrew Vorakrajangthiti
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = TABLE_MESSAGES)
data class AppMessage(
        @PrimaryKey(autoGenerate = true) var id: Int,
        @ColumnInfo(name = COLUMN_MESSAGE_TITLE) var title: String = "",
        @ColumnInfo(name = COLUMN_MESSAGE_BODY) var message: String = "",
        @ColumnInfo(name = COLUMN_MESSAGE_TYPE) var type: String?,
        @ColumnInfo(name = COLUMN_POSTED) var posted: Date?,
        @ColumnInfo(name = COLUMN_CREATED) var created: Date?
): Parcelable

internal const val TABLE_MESSAGES = "app_messages"
internal const val COLUMN_CREATED = "created_at"
internal const val COLUMN_POSTED = "posted_at"
internal const val COLUMN_MESSAGE_BODY = "message"
internal const val COLUMN_MESSAGE_TITLE = "title"
internal const val COLUMN_MESSAGE_TYPE = "type"