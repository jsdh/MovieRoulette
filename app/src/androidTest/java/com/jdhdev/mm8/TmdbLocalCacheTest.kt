package com.jdhdev.mm8

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jdhdev.mm8.data.Movie
import com.jdhdev.mm8.data.source.local.TheMovieDBLocalSource
import com.jdhdev.mm8.data.source.local.database.AppDatabase
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TmdbLocalCacheTest {
    private lateinit var appDb: AppDatabase
    private lateinit var source: TheMovieDBLocalSource

    @Before
    fun initSource() {
        val context = InstrumentationRegistry.getContext()
        appDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .addCallback(AppDatabase)
                .build()

        source = TheMovieDBLocalSource(appDb.getTMDbConfigDao())
    }

    @After
    fun closeDb() {
        appDb.close()
    }

    @Test
    fun populateLocalMoviesCache() {
        val testMovies = listOf(Movie(1, "t", "77", "bg", "poster"),
                Movie(2, "t", "77", "bg", "poster"),
                Movie(3, "t", "77", "bg", "poster"))

        source.refreshCache(testMovies).test().apply {
            awaitTerminalEvent()
            assertNoErrors()
        }
        source.movies.test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { movieList ->
                movieList.map { it.id }
                                .toSet().let { it.size == 3 && it == setOf(1, 2, 3) }
                    }
        }
    }

    @Test
    fun refreshDisplayedMovies() {
        val testMovies = listOf(Movie(1, "t", "77", "bg", "poster"),
                Movie(2, "t", "77", "bg", "poster"),
                Movie(3, "t", "77", "bg", "poster"),
                Movie(4, "t", "77", "bg", "poster"),
                Movie(5, "t", "77", "bg", "poster"),
                Movie(6, "t", "77", "bg", "poster"))

        val match = { obs: TestObserver<List<Movie>>, solution: Set<Int> ->
            obs.awaitTerminalEvent()
            obs.assertNoErrors()
                    .assertValue {
                        it.map { it.id }
                                .toSet() == solution
                    }
        }

        source.refreshCache(testMovies).test().apply {
            awaitTerminalEvent()
            assertNoErrors()
        }
        match(source.movies.test(), setOf(1,2,3))

        source.refreshMovies().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
        }
        match(source.movies.test(), setOf(4,5,6))

        source.refreshMovies().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
        }
        match(source.movies.test(), emptySet())
    }

}