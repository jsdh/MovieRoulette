package com.jdhdev.mm8.data.source.local.database.entity

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase


@Entity(tableName = "tmdb_movie_config")
data class TheMovieDbConfig(
        @PrimaryKey
        var id: Int = 1,
        @ColumnInfo(name = "start_year")
        var startYear: Int = 1950,
        @ColumnInfo(name = "end_year")
        var endYear: Int = 2020,
        @ColumnInfo(name = "rating")
        var rating: String = "",
        @ColumnInfo(name = "genres")
        var genres: String = "",
        @ColumnInfo(name = "include_adult")
        var includeAdult: Boolean = false) {

    companion object {
        @JvmStatic fun insertDefaults(db: SupportSQLiteDatabase) {
            val cv = ContentValues(1)
            cv.put("start_year", 1950)
            cv.put("end_year", 2020)
            cv.put("rating", "ANY")
            cv.put("genres", "")
            cv.put("include_adult", false)

            db.insert("tmdb_movie_config", SQLiteDatabase.CONFLICT_ABORT, cv)
        }
    }
}