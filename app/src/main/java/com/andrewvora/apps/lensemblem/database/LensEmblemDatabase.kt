package com.andrewvora.apps.lensemblem.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.andrewvora.apps.lensemblem.models.*
import com.andrewvora.apps.lensemblem.database.converters.RoomDateConverter


internal const val DB_NAME = "lens_emblem"

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Database(version = 1, entities = [
    Hero::class,
    NameAlias::class,
    TitleAlias::class,
    Stats::class,
    AppMessage::class,
    Bounds::class
])
@TypeConverters(RoomDateConverter::class)
abstract class LensEmblemDatabase : RoomDatabase() {
    abstract fun heroDao(): HeroDao
    abstract fun aliasDao(): AliasDao
    abstract fun statsDao(): StatsDao
    abstract fun messageDao(): MessageDao
    abstract fun boundsDao(): BoundsDao
}