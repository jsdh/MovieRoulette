package com.jdhdev.mm8.data.source;


import com.jdhdev.mm8.data.Movie;
import com.jdhdev.mm8.data.Remote;
import com.jdhdev.mm8.data.source.local.TheMovieDBLocalSource;
import com.jdhdev.mm8.util.BaseSchedulerProvider;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.jdhdev.mm8.util.RxUtils.logSource;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Singleton
public class MoviesRepository implements MovieDataSource {
    private final MovieDataSource remoteDataSource;
    private final MovieDataSource localDataSource;
    private final BaseSchedulerProvider schedulerProvider;


    @Inject
    public MoviesRepository(@Remote MovieDataSource remote,
                            @Local MovieDataSource local,
                            BaseSchedulerProvider scheduler) {
        remoteDataSource = remote;
        localDataSource = local;
        schedulerProvider = scheduler;
    }

    private Observable<List<Movie>> getMoviesList() {
        return Observable.concat(
                    getCachedSource()
                            .filter(mlsit -> mlsit.size() >= 3)
                            .compose(logSource("MOVIES-CACHE")),
                    getMoviesFromRemoteSource())
                .filter(movies -> !movies.isEmpty())
                .firstElement()
                .toObservable();
    }

    private Observable<List<Movie>> getMoviesFromRemoteSource() {
        return Observable.concat(
                    remoteDataSource.getMovies()
                            .compose(logSource("NETWORK"))
                            .flatMap(this::updateCache)
                            .flatMap(ignored -> Observable.empty()),
                    getCachedSource())
                .subscribeOn(schedulerProvider.io());
    }

    @Override
    public Observable<List<Movie>> getMovies() {
        return getMoviesList();
    }

    @Override
    public Observable<Movie> getMovieDetails(int id) {
        return Observable.concat(
                    Observable.empty(), //localDataSource.getMovieDetails(id), TODO -- bug: Discover -> cache -> details -> false cache hit!
                    remoteDataSource.getMovieDetails(id))
                .firstElement()
                .toObservable()
                .subscribeOn(schedulerProvider.io());
    }

    @Override
    public Completable refreshMovies() {
        return localDataSource.refreshMovies()
                .mergeWith(remoteDataSource.refreshMovies());
    }

    @Override
    public Completable resetMovieData() {
        return localDataSource.resetMovieData()
                .mergeWith(remoteDataSource.resetMovieData());
    }

    private Observable<List<Movie>> getCachedSource() {
        return localDataSource.getMovies();
    }


    private Observable<?> updateCache(List<Movie> movies) {
        // TODO -- ugly cast
        return ((TheMovieDBLocalSource)localDataSource).refreshCache(movies)
                .toObservable();
    }
}
