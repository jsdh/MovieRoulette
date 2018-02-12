package com.jdhdev.mm8.data.source.local.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "displayed_movies",
        foreignKeys = arrayOf(ForeignKey(entity = SavedDiscoverMovie::class,
                                         parentColumns = arrayOf("tmdb_id"),
                                         childColumns = arrayOf("movie_id"),
                                         onDelete = ForeignKey.CASCADE)))
data class DisplayedMovie(
        @PrimaryKey
        @ColumnInfo(name = "movie_id")
        var movieId: Int = -1)