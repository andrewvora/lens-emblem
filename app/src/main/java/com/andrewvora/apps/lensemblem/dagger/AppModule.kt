package com.andrewvora.apps.lensemblem.dagger

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import com.andrewvora.apps.lensemblem.database.DB_NAME
import com.andrewvora.apps.lensemblem.database.LensEmblemDatabase
import com.andrewvora.apps.lensemblem.database.migrations.MigrationV1ToV2
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Created on 3/3/2018.
 * @author Andrew Vorakrajangthiti
 */
@Module
class AppModule(private val app: Application) {

    private val database = Room
            .databaseBuilder(app, LensEmblemDatabase::class.java, DB_NAME)
            .addMigrations(MigrationV1ToV2())
            .build()

    @Provides @Singleton
    fun providesApp(): Application {
        return app
    }

    @Provides @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides @Reusable
    fun providesGson(): Gson {
        return GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create()
    }

    @Provides @Singleton
    fun providesDatabase(): LensEmblemDatabase {
        return database
    }

    @Provides
    fun providesSharedPreferences(): SharedPreferences {
        val appPreferencesFile = "lensEmblemPreferences"
        return app.getSharedPreferences(appPreferencesFile, Context.MODE_PRIVATE)
    }
}