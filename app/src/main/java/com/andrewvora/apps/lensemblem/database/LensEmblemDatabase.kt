package com.andrewvora.apps.lensemblem.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.andrewvora.apps.lensemblem.models.Hero
import com.andrewvora.apps.lensemblem.models.NameAlias
import com.andrewvora.apps.lensemblem.models.TitleAlias


internal const val DB_NAME = "lens_emblem"

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Database(version = 1, entities = [
    Hero::class,
    NameAlias::class,
    TitleAlias::class
])
abstract class LensEmblemDatabase : RoomDatabase() {
    abstract fun heroDao(): HeroDao
    abstract fun aliasDao(): AliasDao
}