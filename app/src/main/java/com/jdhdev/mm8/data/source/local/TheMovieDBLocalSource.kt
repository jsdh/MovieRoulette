package com.jdhdev.mm8.data.source.local


import com.jdhdev.mm8.data.Movie
import com.jdhdev.mm8.data.source.MovieDataSource
import com.jdhdev.mm8.data.source.local.database.TheMovieDbDao
import com.jdhdev.mm8.data.source.local.database.entity.DisplayedMovie
import com.jdhdev.mm8.data.source.local.database.entity.SavedDiscoverMovie
import io.reactivex.Completable

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class TheMovieDBLocalSource @Inject constructor(private val dao: TheMovieDbDao) : MovieDataSource {

    override fun getMovies() =
            dao.getDisplayedMovies()
                .toObservable()
                .map { it.map { Movie.from(it) } }
                .subscribeOn(Schedulers.io())



    // TODO --caching details not supported yet
    override fun getMovieDetails(id: Int) = Observable.empty<Movie>()

    fun refreshCache(movies: List<Movie>) =
            Completable.fromAction {
                resetMovieData()          // This is a side-effect.. not idiomatic rx
                dao.addDiscoveredMovies(
                        movies.map {
                            SavedDiscoverMovie(
                                    it.id,
                                    it.title ?: "",
                                    it.backdrop ?: "",
                                    it.posterPath ?: "",
                                    it.overview ?: "",
                                    it.releaseDate ?: "")
                        })
            }.mergeWith(
                    dao.getDiscoveredMovieSet()
                            .flatMapCompletable{ sdMovies ->
                                Completable.fromAction {
                                    dao.addDisplayedMovies(
                                            sdMovies.map { DisplayedMovie(it.tmdbId) })
                                }
                            }

                    )

    // Note: rest of the app assumes this method works synchronously
    //  if need to change it to async - return a completable
    override fun refreshMovies() =
            dao.getDisplayedMovies()
                .map { dao.removeDiscoveredMovies(it) }
                .toCompletable()
                .mergeWith(
                    dao.getDiscoveredMovieSet()
                            .map { discovered ->
                                dao.addDisplayedMovies(
                                        discovered.map { DisplayedMovie(it.tmdbId) }) }
                            .toCompletable())


    override fun resetMovieData() = Completable.fromCallable(dao::clearDiscoveredMovies)

}
