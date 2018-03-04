package com.andrewvora.apps.lensemblem.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.andrewvora.apps.lensemblem.models.*

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Dao
interface AliasDao {

    @Query("SELECT * FROM ${TABLE_TITLE_ALIAS}")
    fun getTitleAliases(): List<TitleAlias>

    @Query("SELECT * FROM ${TABLE_TITLE_ALIAS} WHERE ${COLUMN_CAPTURED_TEXT} LIKE :capturedText")
    fun getTitleAliases(capturedText: String): List<TitleAlias>

    @Query("SELECT * FROM ${TABLE_NAME_ALIAS}")
    fun getNameAliases(): List<NameAlias>

    @Query("SELECT * FROM ${TABLE_NAME_ALIAS} WHERE ${COLUMN_CAPTURED_TEXT} LIKE :capturedText")
    fun getNameAliases(capturedText: String): List<NameAlias>

    @Insert
    fun insert(vararg nameAlias: NameAlias)

    @Insert
    fun insert(vararg titleAlias: TitleAlias)

    @Delete
    fun delete(nameAlias: NameAlias)

    @Delete
    fun delete(titleAlias: TitleAlias)
}