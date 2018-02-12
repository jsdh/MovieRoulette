package com.jdhdev.mm8.presentation

import com.jdhdev.mm8.data.*
import com.jdhdev.mm8.movies.MoviesContract
import com.jdhdev.mm8.movies.MoviesContract.View.Companion.MOVIE_SLOT_1
import com.jdhdev.mm8.movies.MoviesContract.View.Companion.MOVIE_SLOT_2
import com.jdhdev.mm8.movies.MoviesContract.View.Companion.MOVIE_SLOT_3
import com.jdhdev.mm8.movies.MoviesPresenter
import com.jdhdev.mm8.util.BaseSchedulerProvider
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test


class MoviesPresenterTest {
    private lateinit var moviePresenter: MoviesPresenter

    private val view: MoviesContract.View = mock()
    private val data: DataManager = mock()
    private val testScheduler = mock<BaseSchedulerProvider> {
        on { computation() }.doReturn(Schedulers.trampoline())
        on { io() }.doReturn(Schedulers.trampoline())
        on { ui() }.doReturn(Schedulers.trampoline())
    }
    private val defaultTestModel = Model(Movie.EMPTY, Movie.EMPTY, Movie.EMPTY,
            true, true, true,
            true, true, true)

    @Before
    fun setUp() {
        moviePresenter = MoviesPresenter(testScheduler, data, view)
    }

    @Test
    fun createPresenter_bindsToTheView() {
        moviePresenter = MoviesPresenter(testScheduler, data, view)
        verify(view).setPresenter(moviePresenter)
    }

    @Test(expected = IllegalStateException::class)
    fun subscribe_multipleCallsThrowsException() {
        setupDataModelStream()
        moviePresenter.subscribe()
        moviePresenter.subscribe()
    }

    @Test
    fun unsubscribe_disposesOfActiveSubscription() {
        setupDataModelStream()
        moviePresenter.subscribe()
        moviePresenter.unsubscribe()
        moviePresenter.subscribe() // if old subscription not disposed, throws exception
    }

    @Test
    fun subscribe_bindsToModelAndConfigUpdates() {
        setupDataModelStream()
        moviePresenter.subscribe()

        verify(data).modelStream
        verify(data).configuration
    }

    @Test
    fun subscribe_updatesViewOnModelUpdates() {
        val testModel = setupDataModelStream().first

        moviePresenter.subscribe()
        verify(view).movieSlot1 = testModel.movieA
        verify(view).movieSlot2 = testModel.movieB
        verify(view).movieSlot3 = testModel.movieC
        verify(view).setLoadingMovieIndicator(MOVIE_SLOT_1, testModel.loadingMovieA)
        verify(view).setLoadingMovieIndicator(MOVIE_SLOT_2, testModel.loadingMovieB)
        verify(view).setLoadingMovieIndicator(MOVIE_SLOT_3, testModel.loadingMovieC)
        verify(view).setLoadingScoresIndicator(MOVIE_SLOT_1, testModel.loadingImdbInfoA)
        verify(view).setLoadingScoresIndicator(MOVIE_SLOT_2, testModel.loadingImdbInfoB)
        verify(view).setLoadingScoresIndicator(MOVIE_SLOT_3, testModel.loadingImdbInfoC)
        verify(view).setLoadingIndicator(testModel.loadingMovieA ||
                testModel.loadingMovieB || testModel.loadingMovieC)
    }

    @Test
    fun subscribe_updatesViewOnConfigUpdates() {
        val testConfig = setupDataModelStream().second

        moviePresenter.subscribe()
        verify(view).configState = testConfig
    }

    fun setupDataModelStream(model : Model = defaultTestModel,
                             config : TmdbConfiguration = TmdbConfiguration()): Pair<Model, TmdbConfiguration> {
        whenever(data.configuration).thenReturn(Single.just(config))
        whenever(data.modelStream).thenReturn(Observable.just(model))
        return Pair(model, config)
    }
}