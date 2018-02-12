package com.jdhdev.mm8.data.source;

import com.jdhdev.mm8.data.Movie;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface MovieDataSource {
    Observable<List<Movie>> getMovies();
    Observable<Movie> getMovieDetails(int id);

    Completable refreshMovies();
    Completable resetMovieData();
}
