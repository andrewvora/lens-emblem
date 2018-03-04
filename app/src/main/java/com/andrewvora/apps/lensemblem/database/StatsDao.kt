package com.andrewvora.apps.lensemblem.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.andrewvora.apps.lensemblem.models.COLUMN_HERO_ID
import com.andrewvora.apps.lensemblem.models.COLUMN_LEVEL
import com.andrewvora.apps.lensemblem.models.Stats
import com.andrewvora.apps.lensemblem.models.TABLE_HERO_STATS

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Dao
interface StatsDao {
    @Query("SELECT * FROM $TABLE_HERO_STATS")
    fun getStats(): List<Stats>

    @Query("SELECT * FROM $TABLE_HERO_STATS WHERE $COLUMN_HERO_ID like :heroId")
    fun getStats(heroId: Int): List<Stats>

    @Query("SELECT * FROM $TABLE_HERO_STATS WHERE $COLUMN_HERO_ID like :heroId AND $COLUMN_LEVEL like :level")
    fun getStats(heroId: Int, level: Int): List<Stats>

    @Insert
    fun insert(vararg stats: Stats)

    @Delete
    fun delete(stats: Stats)
}