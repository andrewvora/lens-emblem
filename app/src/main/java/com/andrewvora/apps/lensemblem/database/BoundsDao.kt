package com.andrewvora.apps.lensemblem.database

import android.arch.persistence.room.*
import com.andrewvora.apps.lensemblem.models.Bounds
import com.andrewvora.apps.lensemblem.models.BoundsType
import com.andrewvora.apps.lensemblem.models.COLUMN_BOUNDS_TYPE
import com.andrewvora.apps.lensemblem.models.TABLE_BOUNDS

/**
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
@Dao
interface BoundsDao {
    @Query("SELECT * FROM $TABLE_BOUNDS")
    fun getBounds(): List<Bounds>

    @Query("SELECT * FROM $TABLE_BOUNDS WHERE $COLUMN_BOUNDS_TYPE=:type LIMIT 1")
    fun getBounds(type: BoundsType): Bounds?

    @Insert
    fun insert(vararg bounds: Bounds)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg bounds: Bounds)

    @Delete
    fun delete(bounds: Bounds)

    @Query("DELETE FROM $TABLE_BOUNDS WHERE 1=1")
    fun deleteAll()
}