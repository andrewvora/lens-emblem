package com.andrewvora.apps.lensemblem.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.andrewvora.apps.lensemblem.models.COLUMN_NAME
import com.andrewvora.apps.lensemblem.models.COLUMN_TITLE
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.TABLE_HERO

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Dao
interface HeroDao {

    @Query("SELECT * FROM $TABLE_HERO")
    fun getHeroes(): List<Hero>

    @Query("SELECT * FROM $TABLE_HERO WHERE $COLUMN_TITLE like :title AND $COLUMN_NAME like :name")
    fun getHeroes(title: String, name: String): List<Hero>

    @Query("SELECT * FROM $TABLE_HERO WHERE $COLUMN_ID = :id LIMIT 1")
    fun getHero(id: Long): Hero

    @Insert
    fun insert(vararg hero: Hero)

    @Delete
    fun delete(hero: Hero)

    @Query("DELETE FROM $TABLE_HERO WHERE 1=1")
    fun deleteAll()
}

private const val COLUMN_ID = "id"