package com.andrewvora.apps.lensemblem.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.andrewvora.apps.lensemblem.models.AppMessage
import com.andrewvora.apps.lensemblem.models.TABLE_MESSAGES

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
@Dao
interface MessageDao {

    @Query("SELECT * FROM $TABLE_MESSAGES")
    fun getMessages(): List<AppMessage>

    @Insert
    fun insert(vararg message: AppMessage)

    @Delete
    fun delete(message: AppMessage)

    @Query("DELETE FROM $TABLE_MESSAGES WHERE 1=1")
    fun deleteAllMessages()
}