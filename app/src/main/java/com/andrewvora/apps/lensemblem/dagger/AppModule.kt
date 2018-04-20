package com.andrewvora.apps.lensemblem.dagger

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import com.andrewvora.apps.lensemblem.database.DB_NAME
import com.andrewvora.apps.lensemblem.database.LensEmblemDatabase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
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
            .build()

    @Provides @Singleton
    fun providesApp(): Application {
        return app
    }

    @Provides @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides @Singleton
    fun providesGson(): Gson {
        return Gson()
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