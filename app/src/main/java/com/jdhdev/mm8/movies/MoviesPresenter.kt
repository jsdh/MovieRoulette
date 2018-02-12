package com.jdhdev.mm8.movies

import com.jdhdev.mm8.data.DataManager
import com.jdhdev.mm8.data.Model
import com.jdhdev.mm8.data.TmdbConfiguration
import com.jdhdev.mm8.util.BaseSchedulerProvider
import com.jdhdev.mm8.util.RxUtils

import io.reactivex.disposables.Disposable

import com.jdhdev.mm8.movies.MoviesContract.View.Companion.MOVIE_SLOT_1
import com.jdhdev.mm8.movies.MoviesContract.View.Companion.MOVIE_SLOT_2
import com.jdhdev.mm8.movies.MoviesContract.View.Companion.MOVIE_SLOT_3

class MoviesPresenter(private val schedulerProvider: BaseSchedulerProvider,
                      private val dataManager: DataManager,
                      private val moviesView: MoviesContract.View) : MoviesContract.Presenter {
    private var modelSub: Disposable? = null

    init {
        moviesView.setPresenter(this)
    }

    override fun subscribe() {
        if (modelSub != null) {
            throw IllegalStateException("MP called sub with active model subscription")
        }

        modelSub = dataManager.modelStream
                .compose(RxUtils.logSource("MP::UI-Model"))
                .observeOn(schedulerProvider.ui())
                .subscribe(::updateUi)

        dataManager.configuration
                .observeOn(schedulerProvider.ui())
                .subscribe(::updateConfigUi)
    }

    private fun updateUi(model: Model) {
        moviesView.movieSlot1 = model.movieA
        moviesView.movieSlot2 = model.movieB
        moviesView.movieSlot3 = model.movieC
        moviesView.setLoadingMovieIndicator(MOVIE_SLOT_1, model.loadingMovieA)
        moviesView.setLoadingMovieIndicator(MOVIE_SLOT_2, model.loadingMovieB)
        moviesView.setLoadingMovieIndicator(MOVIE_SLOT_3, model.loadingMovieC)
        moviesView.setLoadingScoresIndicator(MOVIE_SLOT_1, model.loadingImdbInfoA)
        moviesView.setLoadingScoresIndicator(MOVIE_SLOT_2, model.loadingImdbInfoB)
        moviesView.setLoadingScoresIndicator(MOVIE_SLOT_3, model.loadingImdbInfoC)

        moviesView.setLoadingIndicator(model.loadingMovieA ||
                model.loadingMovieB || model.loadingMovieC)
    }

    private fun updateConfigUi(config: TmdbConfiguration) {
        moviesView.configState = config
    }

    override fun unsubscribe() {
        modelSub?.dispose()
        modelSub = null
    }

    override fun showMovieDetails(slot: Int) {
        moviesView.showMovieDetailsUi(slot)
    }

    override fun refreshMovies() {
        dataManager.findNewRandomMovies(true)
    }

    override fun onMovieConfigUpdate(config: TmdbConfiguration) {
        dataManager.saveConfiguration(config)
    }
}
