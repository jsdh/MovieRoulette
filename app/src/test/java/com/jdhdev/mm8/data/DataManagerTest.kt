package com.jdhdev.mm8.data

import com.jdhdev.mm8.data.source.MovieDataSource
import com.jdhdev.mm8.data.source.ScoresDataSource
import com.jdhdev.mm8.data.source.TheMovieDbConfigSource
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo
import com.jdhdev.mm8.util.BaseSchedulerProvider
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test


class DataManagerTest {
    private lateinit var dataManager: DataManager

    private val movieRepo = mock<MovieDataSource> {
        on { refreshMovies() }.doReturn(Completable.complete())
        on { movies }.doReturn(Observable.just(mutableListOf(Movie.EMPTY, Movie.EMPTY, Movie.EMPTY)))
        on { getMovieDetails(any())}.doReturn(Observable.just(Movie.EMPTY))
    }
    private val scoreRepo: ScoresDataSource = mock()
    private val cSource: TheMovieDbConfigSource = mock()
    private val testScheduler = mock<BaseSchedulerProvider> {
        on { computation() }.doReturn(Schedulers.trampoline())
        on { io() }.doReturn(Schedulers.trampoline())
        on { ui() }.doReturn(Schedulers.trampoline())
    }

    @Before
    fun setUp() {
        whenever(movieRepo.refreshMovies()).thenReturn(Completable.complete())
        whenever(movieRepo.movies).thenReturn(Observable.just(mutableListOf(Movie.EMPTY)))
        dataManager = DataManagerImpl(movieRepo, scoreRepo, cSource, testScheduler)
    }

    @Test
    fun createDataManager_requestsMoviesFromRepo() {
        val movies = mock<MovieDataSource> {
            on { refreshMovies() }.doReturn(Completable.complete())
            on { movies }.doReturn(Observable.just(mutableListOf(Movie.EMPTY)))
        }
        val dm = DataManagerImpl(movies, mock(), mock(), testScheduler)

        verify(movies).movies

        dm.modelStream.test().apply {
            assertNoErrors()
            assertValueCount(1) //
            //assertValueAt(0, { it.movieA == Movie.EMPTY })
        }
    }

    @Test
    fun onCreate_InitializesToEmptyState() {
        dataManager.modelStream.test().apply {
            assertNoErrors()
            assertValueCount(1)
            assertValueAt(0, { isInitialState(it) })
        }
    }

    @Test
    fun findNewRandomMovies_requestsMoviesFromRepoAndEmitsLoadingThenTheMovies() {
        val testMovie = Movie(42, "A Movie", "", "", "")
        whenever(movieRepo.movies).doReturn(Observable.just(mutableListOf(testMovie, Movie.EMPTY, Movie.EMPTY)))

        val testSub = dataManager.modelStream.test();
        dataManager.findNewRandomMovies(false)

        testSub.apply {
            assertNoErrors()
            assertValueAt(1, {it.loadingMovieA && it.loadingMovieB && it.loadingMovieC})
            assertValueAt(2, {it.movieA == testMovie})
            //assertValueCount(3) // Initial -> LOADING -> Movies
        }
    }

    @Test
    fun findNewRandomMovies_forceUpdateCallsRefreshMoviesOnDataSource() {
        dataManager.findNewRandomMovies(true)

        verify(movieRepo).refreshMovies()
        verify(movieRepo, atLeast(2)).movies // one on create, one on method call
    }

    @Test
    fun findNewRandomMovies_newMoviesTriggerDetailsRequest() {
        val testSub = dataManager.modelStream.test()
        val testMovie = Movie(42, "A Movie", "", "", "")
        val testMovieDetails = Movie(42, "A Movie Detail", "", "", "")
        whenever(movieRepo.movies).doReturn(Observable.just(mutableListOf(Movie.EMPTY, testMovie, Movie.EMPTY)))
        whenever(movieRepo.getMovieDetails(any())).doReturn(Observable.just(testMovieDetails))

        dataManager.findNewRandomMovies(false)
        testSub.apply {
            assertNoErrors()
            assertValueCount(5) // initial -> loading -> discover -> loading -> details
            assertValueAt(2, {
                it.movieA == Movie.EMPTY &&
                it.movieB == testMovie &&
                it.movieC == Movie.EMPTY &&
                !it.loadingMovieA &&
                !it.loadingMovieB &&
                !it.loadingMovieC })
            assertValueAt(3, { it.movieB == testMovie && it.loadingMovieB })
            assertValueAt(4, { it.movieB == testMovieDetails && !it.loadingMovieB })
        }
    }

    @Test
    fun findNewRandomMovies_movieDetailsWithImdbIdTriggerImdbLookup() {
        val testSub = dataManager.modelStream.test()

        val testMovie = Movie(42, "A Movie", "", "", "")
        val testMovieDetails = Movie(42, "A Movie Detail", "", "", "")
                .apply { imdbId = "IdIdId" }
        val testImdbInfo = ImdbMovieInfo("IdIdId", "4.2", "2.4", mutableListOf())
        whenever(movieRepo.movies).doReturn(Observable.just(mutableListOf(Movie.EMPTY, Movie.EMPTY, testMovie)))
        whenever(movieRepo.getMovieDetails(any())).doReturn(Observable.just(testMovieDetails))
        whenever(scoreRepo.getScore("IdIdId")).doReturn(Observable.just(testImdbInfo))

        dataManager.findNewRandomMovies(false)
        testSub.apply {
            assertNoErrors()
            assertValueCount(7)
            assertValueAt(5, { it.movieC == testMovieDetails && it.loadingImdbInfoC })
            assertValueAt(6, { !it.loadingImdbInfoC && it.movieC.imdbScore == testImdbInfo.rating })
        }
    }

    @Test
    fun saveConfiguration_callsConfigSourceAndResetsMovieData() {
        val tconfig = TmdbConfiguration()
        whenever(movieRepo.resetMovieData()).doReturn(Completable.complete())
        whenever(cSource.setConfig(tconfig)).doReturn(Completable.complete())

        dataManager.saveConfiguration(tconfig)
        verify(movieRepo).resetMovieData()
        verify(cSource).setConfig(tconfig)
    }



    fun isInitialState(state: Model): Boolean = !state.loadingMovieA && !state.loadingMovieB &&
            !state.loadingMovieC && !state.loadingImdbInfoA && !state.loadingImdbInfoB &&
            !state.loadingImdbInfoC && state.movieA == Movie.EMPTY && state.movieB == Movie.EMPTY &&
            state.movieC == Movie.EMPTY;
}
