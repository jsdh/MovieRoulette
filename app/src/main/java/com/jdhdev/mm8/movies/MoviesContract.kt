package com.jdhdev.mm8.movies

import com.jdhdev.mm8.BasePresenter
import com.jdhdev.mm8.BaseView
import com.jdhdev.mm8.data.Movie
import com.jdhdev.mm8.data.TmdbConfiguration

interface MoviesContract {
    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)
        fun setLoadingMovieIndicator(slot: Int, active: Boolean)
        fun setLoadingScoresIndicator(slot: Int, active: Boolean)
        fun showLoadingMoviesError()
        fun showMovieDetailsUi(slot: Int)

        var movieSlot1: Movie
        var movieSlot2: Movie
        var movieSlot3: Movie
        var configState: TmdbConfiguration

        companion object {
            const val MOVIE_SLOT_1 = 1
            const val MOVIE_SLOT_2 = 2
            const val MOVIE_SLOT_3 = 3
        }
    }

    interface Presenter : BasePresenter {
        fun refreshMovies()
        fun showMovieDetails(slot: Int)
        fun onMovieConfigUpdate(config: TmdbConfiguration)
    }
}
