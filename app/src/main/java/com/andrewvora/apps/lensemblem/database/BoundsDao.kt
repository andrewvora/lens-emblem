package com.andrewvora.apps.lensemblem.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.andrewvora.apps.lensemblem.models.Bounds
import com.andrewvora.apps.lensemblem.models.TABLE_BOUNDS

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
@Dao
interface BoundsDao {
    @Query("SELECT * FROM $TABLE_BOUNDS")
    fun getBounds(): List<Bounds>

    @Insert
    fun insert(vararg bounds: Bounds)

    @Delete
    fun delete(bounds: Bounds)

    @Query("DELETE FROM $TABLE_BOUNDS WHERE 1=1")
    fun deleteAll()
}