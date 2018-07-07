package com.andrewvora.apps.lensemblem.database.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import com.andrewvora.apps.lensemblem.models.*

/**
 * Created on 7/7/2018.
 * @author Andrew Vorakrajangthiti
 */
class MigrationV2ToV3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // add weapon type column
        database.execSQL("ALTER TABLE $TABLE_HERO ADD COLUMN $COLUMN_WEAPON_TYPE INTEGER")

        // add weapon type URL column
        database.execSQL("ALTER TABLE $TABLE_HERO ADD COLUMN $COLUMN_WEAPON_TYPE_URL TEXT")

        // add movement type column
        database.execSQL("ALTER TABLE $TABLE_HERO ADD COLUMN $COLUMN_MOVEMENT_TYPE INTEGER")

        // add movement type URL column
        database.execSQL("ALTER TABLE $TABLE_HERO ADD COLUMN $COLUMN_MOVEMENT_TYPE_URL TEXT")
    }
}