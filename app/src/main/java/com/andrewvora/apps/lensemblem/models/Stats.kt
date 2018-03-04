package com.andrewvora.apps.lensemblem.models

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


internal const val TABLE_HERO_STATS = "hero_stats_v1"

internal const val COLUMN_EQUIPPED = "equipped"
internal const val COLUMN_LEVEL = "level"
internal const val COLUMN_HP = "hp"
internal const val COLUMN_ATK = "atk"
internal const val COLUMN_SPD = "spd"
internal const val COLUMN_DEF = "def"
internal const val COLUMN_RES = "res"
internal const val COLUMN_SP = "sp"
internal const val COLUMN_HM = "hm"

internal const val COLUMN_HERO_ID = "hero_id"

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = TABLE_HERO_STATS,
        foreignKeys = [
            ForeignKey(
                    entity = Hero::class,
                    parentColumns = ["id"],
                    childColumns = [COLUMN_HERO_ID],
                    onDelete = CASCADE)
        ])
data class Stats(
        @PrimaryKey
        var id: Int,
        @SerializedName("level")
        @ColumnInfo(name = COLUMN_LEVEL)
        var level: Int,
        @SerializedName("hp")
        @ColumnInfo(name = COLUMN_HP)
        var hp: Int,
        @SerializedName("atk")
        @ColumnInfo(name = COLUMN_ATK)
        var atk: Int,
        @SerializedName("spd")
        @ColumnInfo(name = COLUMN_SPD)
        var spd: Int,
        @SerializedName("def")
        @ColumnInfo(name = COLUMN_DEF)
        var def: Int,
        @SerializedName("res")
        @ColumnInfo(name = COLUMN_RES)
        var res: Int,
        @SerializedName("sp")
        @ColumnInfo(name = COLUMN_SP)
        var sp: Int,
        @SerializedName("hm")
        @ColumnInfo(name = COLUMN_HM)
        var hm: Int,
        @SerializedName("equipped")
        @ColumnInfo(name = COLUMN_EQUIPPED)
        var equipped: Boolean,
        @ColumnInfo(name = COLUMN_HERO_ID)
        var heroId: Int
): Parcelable