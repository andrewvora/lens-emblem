package com.andrewvora.apps.lensemblem.models

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

internal const val TABLE_TITLE_ALIAS = "title_aliases"
internal const val TABLE_NAME_ALIAS = "name_aliases"
internal const val COLUMN_CAPTURED_TEXT = "captured_text"
internal const val COLUMN_HERO_TITLE = "hero_title"
internal const val COLUMN_HERO_NAME = "hero_name"

/**
 * Represents the mapping of OCR captured text and a hero's title
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = TABLE_TITLE_ALIAS)
data class TitleAlias(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @ColumnInfo(name = COLUMN_CAPTURED_TEXT) var capturedText: String,
        @ColumnInfo(name = COLUMN_HERO_TITLE) var heroTitle: String
): Parcelable

/**
 * Represents the mapping of OCR captured text and a hero's name
 * @author Andrew Vorakrajangthiti
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = TABLE_NAME_ALIAS)
data class NameAlias(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @ColumnInfo(name = COLUMN_CAPTURED_TEXT) var capturedText: String,
        @ColumnInfo(name = COLUMN_HERO_NAME) var heroName: String
): Parcelable
