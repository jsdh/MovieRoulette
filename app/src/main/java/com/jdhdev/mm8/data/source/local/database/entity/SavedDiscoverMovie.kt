package com.jdhdev.mm8.data.source.local.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "discovered_movies")
data class SavedDiscoverMovie(
        @PrimaryKey
        @ColumnInfo(name = "tmdb_id")
        var tmdbId: Int = -1,
        @ColumnInfo(name = "title")
        var title: String = "",
        @ColumnInfo(name = "backdrop_path")
        var backdropPath: String = "",
        @ColumnInfo(name = "poster_path")
        var posterPath: String = "",
        var overview: String = "",
        @ColumnInfo(name = "release_date")
        var releaseDate: String = "")