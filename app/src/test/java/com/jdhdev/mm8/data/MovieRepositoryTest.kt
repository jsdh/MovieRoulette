package com.jdhdev.mm8.data

import com.jdhdev.mm8.data.source.MovieDataSource
import com.jdhdev.mm8.data.source.MoviesRepository
import com.jdhdev.mm8.util.BaseSchedulerProvider
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import java.util.*


class MovieRepositoryTest {
    private lateinit var movieRepo: MoviesRepository

    private val remoteSource: MovieDataSource = mock()
    private val localSource: MovieDataSource = mock()
    private val testScheduler = mock<BaseSchedulerProvider> {
        on { computation() }.doReturn(Schedulers.trampoline())
        on { io() }.doReturn(Schedulers.trampoline())
        on { ui() }.doReturn(Schedulers.trampoline())
    }

    @Before
    fun setUp() {
        movieRepo = MoviesRepository(remoteSource, localSource, testScheduler)
    }

    @Test
    fun getMovies_remoteNotCalledWhenLocalExists() {
        val localMovie = Movie(42, "local", "", "", "")
        whenever(localSource.movies).doReturn(Observable.just(Arrays.asList(localMovie, Movie.EMPTY, Movie.EMPTY)))
        whenever(remoteSource.movies).doReturn(Observable.just(Arrays.asList(Movie.EMPTY, Movie.EMPTY, Movie.EMPTY)))

        movieRepo.movies.test().apply {
            assertNoErrors()
            assertValueCount(1)
            assertValueAt(0, { it[0] == localMovie })
        }
    }

    @Test
    fun getMovies_remoteIsCalledWhenLocalDoesNotExist() {
        // TODO -- this one is tricky b/c of the interplay between local and remote sources
    }

    @Test
    fun getMoviesDetails_callRemoteSourceGetMovieDetails() {
        val testDetails = Movie(42, "Details", "", "", "")
        whenever(remoteSource.getMovieDetails(42)).doReturn(Observable.just(testDetails))

        movieRepo.getMovieDetails(42).test().apply {
            assertNoErrors()
            assertValueCount(1)
            assertValueAt(0, { it == testDetails })
        }
    }

    @Test
    fun refreshMovies_callsRefreshOnLocalAndRemoteSources() {
        whenever(localSource.refreshMovies()).doReturn(Completable.complete())
        whenever(remoteSource.refreshMovies()).doReturn(Completable.complete())

        movieRepo.refreshMovies().test()

        verify(localSource).refreshMovies()
        verify(remoteSource).refreshMovies()
    }

    @Test
    fun resetMovieData_callsResetOnLocalAndRemoteSources() {
        whenever(localSource.resetMovieData()).doReturn(Completable.complete())
        whenever(remoteSource.resetMovieData()).doReturn(Completable.complete())

        movieRepo.resetMovieData().test()

        verify(localSource).resetMovieData()
        verify(remoteSource).resetMovieData()
    }
}