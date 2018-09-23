package com.andrewvora.apps.lensemblem.database.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

/**
 * Created on 9/23/2018.
 * @author Andrew Vorakrajangthiti
 */
class MigrationV3ToV4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // remove message table
        val messageTableName = "app_messages"
        database.execSQL("DROP TABLE IF EXISTS $messageTableName")
    }
}