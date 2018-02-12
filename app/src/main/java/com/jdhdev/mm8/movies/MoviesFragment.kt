package com.jdhdev.mm8.movies

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window

import com.jdhdev.mm8.R
import com.jdhdev.mm8.data.TmdbConfiguration
import com.jdhdev.mm8.movieDetails.MovieDetailsActivity
import com.jdhdev.mm8.ui.MovieConfig
import com.jdhdev.mm8.ui.MovieTile

import kotlinx.android.synthetic.main.fragment_movies.*


class MoviesFragment : Fragment(), MoviesContract.View {

    companion object {
        fun newInstance() = MoviesFragment()
    }

    private var presenter: MoviesContract.Presenter? = null

    override var movieSlot1
        set(value) = movie_tile_1.bind(value)
        get() = movie_tile_1.movie

    override var movieSlot2
        set(value) = movie_tile_2.bind(value)
        get() = movie_tile_2.movie

    override var movieSlot3
        set(value) = movie_tile_3.bind(value)
        get() = movie_tile_3.movie

    override var configState = TmdbConfiguration()
        set(value) = filters.bind(value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_movies, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).let {
            it.setSupportActionBar(view.findViewById(R.id.toolbar))
            it.supportActionBar?.title = getString(R.string.app_title)
        }

        refresh_movies.setOnClickListener { _ -> presenter?.refreshMovies() }
        movie_tile_1.setOnClickListener { _ -> presenter?.showMovieDetails(MoviesContract.View.Companion.MOVIE_SLOT_1) }
        movie_tile_2.setOnClickListener { _ -> presenter?.showMovieDetails(MoviesContract.View.Companion.MOVIE_SLOT_2) }
        movie_tile_3.setOnClickListener { _ -> presenter?.showMovieDetails(MoviesContract.View.Companion.MOVIE_SLOT_3) }

        filters.setListener(object: MovieConfig.ConfigChangeListener {
            override fun onConfigChanged(config: TmdbConfiguration) {
                onMovieConfigChanged(config)
            }
        })
    }

    private fun onMovieConfigChanged(config: TmdbConfiguration) =
        presenter?.onMovieConfigUpdate(config)

    override fun onResume() {
        super.onResume()
        presenter?.subscribe()
    }

    override fun onPause() {
        presenter?.unsubscribe()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.main, menu)

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_config -> {
                drawer.openDrawer(GravityCompat.END)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    // MoviesContract.View callbacks________________________________________
    override fun setPresenter(p: MoviesContract.Presenter) {
        presenter = p
    }


    override fun setLoadingMovieIndicator(slot: Int, active: Boolean) =
        getMovieTileForSlot(slot).setLoadingMovie(active)

    override fun setLoadingScoresIndicator(slot: Int, active: Boolean) =
        getMovieTileForSlot(slot).setLoadingScores(active)

    override fun setLoadingIndicator(active: Boolean) {
        if (active) {
            refresh_movies.animate()
                    .rotationBy(360f)
                    .scaleX(0f)
                    .scaleY(0f)
        } else {
            refresh_movies.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .withEndAction { refresh_movies?.rotation = 0f }
        }
        refresh_movies.isEnabled = !active
    }
    
    override fun showLoadingMoviesError() {
        TODO("show loading error not implemented yet")
    }

    override fun showMovieDetailsUi(slot: Int) {
        val tile = getMovieTileForSlot(slot)
        val intent = Intent(context, MovieDetailsActivity::class.java).apply {
            putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, slot)
            putExtra(MovieDetailsActivity.EXTRA_TRANSITION_TITLE_STRING, tile.titleView.text)
        }

        val statusBar = activity!!.findViewById<View>(android.R.id.statusBarBackground)
        val navBar = activity!!.findViewById<View>(android.R.id.navigationBarBackground)

        val pairs = arrayOf<Pair<View, String>>(
                Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME),
                Pair.create(navBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME),
                Pair.create(tile.titleView, "title"),
                Pair.create(tile, "backdrop"))

        val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        activity!!,
                        *pairs)

        startActivity(intent, options.toBundle())
    }

    private fun getMovieTileForSlot(slot: Int): MovieTile =
            when (slot) {
                MoviesContract.View.MOVIE_SLOT_1 -> movie_tile_1
                MoviesContract.View.MOVIE_SLOT_2 -> movie_tile_2
                MoviesContract.View.MOVIE_SLOT_3 -> movie_tile_3
                else -> throw  IllegalArgumentException("MF: unknown tile id: $slot")
            }
}
