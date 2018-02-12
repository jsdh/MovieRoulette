package com.jdhdev.mm8.data.source.local.database

import android.arch.persistence.room.*
import com.jdhdev.mm8.data.source.local.database.entity.DisplayedMovie
import com.jdhdev.mm8.data.source.local.database.entity.SavedDiscoverMovie
import com.jdhdev.mm8.data.source.local.database.entity.TheMovieDbConfig
import io.reactivex.Single

@Dao
interface TheMovieDbDao {
    @Query("SELECT * FROM tmdb_movie_config LIMIT 1")
    fun getConfig(): Single<TheMovieDbConfig>

    @Update
    fun updateConfig(config: TheMovieDbConfig)

    @Query("SELECT * FROM discovered_movies LIMIT 3")
    fun getDiscoveredMovieSet(): Single<List<SavedDiscoverMovie>>

    @Query("DELETE FROM discovered_movies")
    fun clearDiscoveredMovies()

    @Insert
    fun addDiscoveredMovies(movies: List<SavedDiscoverMovie>)

    @Delete
    fun removeDiscoveredMovies(movies: List<SavedDiscoverMovie>)

    @Query("SELECT * FROM discovered_movies INNER JOIN displayed_movies ON displayed_movies.movie_id = discovered_movies.tmdb_id")
    fun getDisplayedMovies(): Single<List<SavedDiscoverMovie>>

    @Query("DELETE FROM displayed_movies")
    fun clearDisplayedMovies()

    @Insert
    fun addDisplayedMovies(movies: List<DisplayedMovie>)

//    -- Room does not support INSERT query type yet :(
//    @Query("INSERT INTO displayed_movies (tmdb_id) SELECT 'tmdb_id' FROM discovered_movies LIMIT 3")
//    fun refreshDisplayedMovies()
}