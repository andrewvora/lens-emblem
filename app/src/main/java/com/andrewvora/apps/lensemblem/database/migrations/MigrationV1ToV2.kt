package com.andrewvora.apps.lensemblem.database.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import com.andrewvora.apps.lensemblem.models.COLUMN_IMAGE_URL
import com.andrewvora.apps.lensemblem.models.TABLE_HERO

/**
 * Created on 6/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class MigrationV1ToV2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // add image URL column
        database.execSQL("ALTER TABLE $TABLE_HERO ADD COLUMN $COLUMN_IMAGE_URL TEXT")
    }
}