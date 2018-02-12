package com.jdhdev.mm8.data.source.local.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.jdhdev.mm8.data.source.local.database.entity.DisplayedMovie
import com.jdhdev.mm8.data.source.local.database.entity.SavedDiscoverMovie
import com.jdhdev.mm8.data.source.local.database.entity.TheMovieDbConfig

@Database(entities = arrayOf(TheMovieDbConfig::class,
                             SavedDiscoverMovie::class,
                             DisplayedMovie::class),
        version =  1,
        exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTMDbConfigDao(): TheMovieDbDao

    companion object : RoomDatabase.Callback() {
        const val DB_NAME = "AppDatabase"

        @JvmStatic override fun onCreate(db: SupportSQLiteDatabase) {
            fillDefaults(db)
        }
        @JvmStatic override fun onOpen(db: SupportSQLiteDatabase) {}

        @JvmStatic fun fillDefaults(db: SupportSQLiteDatabase) {
            TheMovieDbConfig.insertDefaults(db)
        }
    }
}