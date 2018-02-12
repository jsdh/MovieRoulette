package com.jdhdev.mm8


import android.arch.persistence.room.Room
import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.jdhdev.mm8.data.source.local.database.AppDatabase
import com.jdhdev.mm8.data.source.local.database.TheMovieDbDao
import com.jdhdev.mm8.data.source.local.database.entity.DisplayedMovie
import com.jdhdev.mm8.data.source.local.database.entity.SavedDiscoverMovie
import com.jdhdev.mm8.data.source.local.database.entity.TheMovieDbConfig

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var appDb: AppDatabase
    private lateinit var tmdDao: TheMovieDbDao

    @get:Rule
    val exceptionCatcher = ExpectedException.none()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getContext()
        appDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .addCallback(AppDatabase)
                .build()
        tmdDao = appDb.getTMDbConfigDao()
    }

    @After
    fun closeDb() {
        appDb.close()
    }

    /**
     * TODO cryptic error msg,
     *  see: https://medium.com/@fabioCollini/testing-asynchronous-rxjava-code-using-mockito-8ad831a16877
     */
    // CONFIG TESTS _______________________________________________________________________________
    @Test
    fun readDefaultConfig() {
        tmdDao.getConfig().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue {
                it.id == 1 &&
                        it.startYear == 1850 &&
                        it.endYear == 2020 &&
                        it.rating == "ANY" &&
                        it.genres == "" &&
                        !it.includeAdult
            }
        }
    }

    @Test
    fun writeNewConfig() {
        val myConfig = TheMovieDbConfig(
                startYear = 1990,
                endYear = 2011,
                rating = "PG-13",
                genres = "BOP,BOOP,BEEP",
                includeAdult = true)

        tmdDao.updateConfig(myConfig)

        tmdDao.getConfig().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it == myConfig }
        }
    }


    // DISCOVERED MOVIES TABLE ____________________________________________________________________
    @Test
    fun readAndWriteDiscoveredMovie() {
        val movie = SavedDiscoverMovie(45,"beepboop", "abackdrop",
                "aposter","aoverview","bob")

        tmdDao.addDiscoveredMovies(listOf(movie))

        tmdDao.getDiscoveredMovieSet().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it[0] == movie }
        }
    }

    @Test
    fun clearAllDiscoveredMovies() {
        val movie1 = SavedDiscoverMovie(45,"beepboop", "abackdrop",
                "aposter","aoverview","bob")
        val movie2 = SavedDiscoverMovie(46,"beepboop", "abackdrop",
                "aposter","aoverview","bob")

        tmdDao.addDiscoveredMovies(listOf(movie1, movie2))

        tmdDao.getDiscoveredMovieSet().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.size == 2 }
        }

        tmdDao.clearDiscoveredMovies()
        tmdDao.getDiscoveredMovieSet().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.isEmpty() }
        }
    }

    @Test
    fun deleteSomeDiscoveredMovies() {
        val movie1 = SavedDiscoverMovie(45,"beepboop", "abackdrop",
                "aposter","aoverview","bob")
        val movie2 = SavedDiscoverMovie(46,"beepboop", "abackdrop",
                "aposter","aoverview","bob")

        tmdDao.addDiscoveredMovies(listOf(movie1, movie2))
        tmdDao.removeDiscoveredMovies(listOf(movie1))

        tmdDao.getDiscoveredMovieSet().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.size == 1 && it[0].tmdbId == movie2.tmdbId }
        }
    }


    // DISPLAYED MOVIES TABLE _____________________________________________________________________
    @Test
    fun verifyDisplayedMoviedForeignKeyConstraint() {
        exceptionCatcher.expect(SQLiteConstraintException::class.java)
        tmdDao.addDisplayedMovies(listOf(DisplayedMovie(45), DisplayedMovie(46)))
    }

    @Test
    fun readAndWriteDisplayedMovie() {
        val movie1 = SavedDiscoverMovie(45,"beepboop", "abackdrop",
                "aposter","aoverview","bob")
        val movie2 = SavedDiscoverMovie(46,"beepboop", "abackdrop",
                "aposter","aoverview","bob")
        val movie3 = SavedDiscoverMovie(47,"beepboop", "abackdrop",
                "aposter","aoverview","bob")
        val movie4 = SavedDiscoverMovie(48,"beepboop", "abackdrop",
                "aposter","aoverview","bob")

        tmdDao.getDisplayedMovies().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.isEmpty() }
        }

        val answer = mutableSetOf(45, 46, 47)

        tmdDao.addDiscoveredMovies(listOf(movie1, movie2, movie3, movie4))
        tmdDao.getDiscoveredMovieSet().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.size == 3 }
            assertValue { it.map { it.tmdbId }.toSet() == answer }
        }

        tmdDao.addDisplayedMovies(answer.map { DisplayedMovie(it) })
        tmdDao.getDisplayedMovies().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.map { it.tmdbId }.toSet() == answer }
        }
    }

    @Test
    fun clearDisplayedMovies() {
        readAndWriteDisplayedMovie()

        tmdDao.getDisplayedMovies().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.size == 3 }
        }

        tmdDao.clearDisplayedMovies()
        tmdDao.getDisplayedMovies().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.isEmpty() }
        }
    }

    @Test
    fun removingDiscoveredMoviesAlsoRemovesThemFromDisplayedMovies() {
        readAndWriteDisplayedMovie()
        tmdDao.getDisplayedMovies().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.size == 3 }
        }
        tmdDao.clearDiscoveredMovies()
            tmdDao.getDisplayedMovies().test().apply {
            awaitTerminalEvent()
            assertNoErrors()
            assertValue { it.isEmpty() }
        }
    }
}
