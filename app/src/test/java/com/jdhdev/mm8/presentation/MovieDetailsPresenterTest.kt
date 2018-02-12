package com.jdhdev.mm8.presentation

import com.jdhdev.mm8.data.DataManager
import com.jdhdev.mm8.data.Model
import com.jdhdev.mm8.data.Movie
import com.jdhdev.mm8.movieDetails.MovieDetailsContract
import com.jdhdev.mm8.movieDetails.MovieDetailsPresenter
import com.jdhdev.mm8.movies.MoviesContract.View.Companion.MOVIE_SLOT_1
import com.jdhdev.mm8.util.BaseSchedulerProvider
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test


class MovieDetailsPresenterTest {
    private lateinit var movieDetailsPresenter: MovieDetailsPresenter

    private val view: MovieDetailsContract.View = mock()
    private val data: DataManager = mock()
    private val testScheduler = mock<BaseSchedulerProvider> {
        on { computation() }.doReturn(Schedulers.trampoline())
        on { io() }.doReturn(Schedulers.trampoline())
        on { ui() }.doReturn(Schedulers.trampoline())
    }
    private val defaultTestModel = Model(
            Movie(42, "fake movie", "", "", ""),
            Movie.EMPTY, Movie.EMPTY,
            true, true, true,
            true, true, true)

    @Before
    fun setUp() {
        movieDetailsPresenter = MovieDetailsPresenter(MOVIE_SLOT_1, testScheduler, data, view)
    }

    @Test
    fun createPresenter_bindsToTheView() {
        movieDetailsPresenter = MovieDetailsPresenter(MOVIE_SLOT_1, testScheduler, data, view)
        verify(view).setPresenter(movieDetailsPresenter)
    }

    @Test(expected = IllegalStateException::class)
    fun subscribe_multipleCallsThrowsException() {
        setupDataModelStream()
        movieDetailsPresenter.subscribe()
        movieDetailsPresenter.subscribe()
    }

    @Test
    fun unsubscribe_disposesOfActiveSubscription() {
        setupDataModelStream()
        movieDetailsPresenter.subscribe()
        movieDetailsPresenter.unsubscribe()
        movieDetailsPresenter.subscribe() // if old subscription not disposed, throws exception
    }

    @Test
    fun subscribe_updatesViewOnModelUpdates() {
        val testModel = setupDataModelStream()

        movieDetailsPresenter.subscribe()
        verify(view).bind(testModel.movieA)
    }

    @Test
    fun subscribe_emptyModel_callsViewShowEmpty() {
        setupDataModelStream(Model())

        movieDetailsPresenter.subscribe()
        verify(view).showMissingDetails()
    }

    fun setupDataModelStream(model: Model = defaultTestModel): Model {
        whenever(data.modelStream).thenReturn(Observable.just(model))
        return model
    }

}