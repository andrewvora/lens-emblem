package com.andrewvora.apps.lensemblem.models

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
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
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @ColumnInfo(name = COLUMN_MESSAGE_TITLE)
        @SerializedName("title")
        var title: String = "",

        @ColumnInfo(name = COLUMN_MESSAGE_BODY)
        @SerializedName("message")
        var message: String = "",

        @ColumnInfo(name = COLUMN_MESSAGE_TYPE)
        @SerializedName("type")
        var type: String?,

        @ColumnInfo(name = COLUMN_POSTED)
        @SerializedName("posted_at")
        var posted: Date?,

        @ColumnInfo(name = COLUMN_CREATED)
        @SerializedName("created_at")
        var created: Date?,

        @ColumnInfo(name = COLUMN_MESSAGE_READ)
        var read: Boolean? = false
): Parcelable

internal const val TABLE_MESSAGES = "app_messages"
internal const val COLUMN_CREATED = "created_at"
internal const val COLUMN_POSTED = "posted_at"
internal const val COLUMN_MESSAGE_BODY = "message"
internal const val COLUMN_MESSAGE_TITLE = "title"
internal const val COLUMN_MESSAGE_TYPE = "type"
internal const val COLUMN_MESSAGE_READ = "read"