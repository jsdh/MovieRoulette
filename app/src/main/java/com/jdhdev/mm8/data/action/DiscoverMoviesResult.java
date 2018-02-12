package com.jdhdev.mm8.data.action;

import com.jdhdev.mm8.data.Model;
import com.jdhdev.mm8.data.Movie;

import java.util.List;


public class DiscoverMoviesResult extends Result {
    public static final DiscoverMoviesResult LOADING =
            new DiscoverMoviesResult(Movie.EMPTY, Movie.EMPTY, Movie.EMPTY);

    public final Movie movie1;
    public final Movie movie2;
    public final Movie movie3;

    private DiscoverMoviesResult(Movie a, Movie b, Movie c) {
        this(a, b, c, null);
    }

    private DiscoverMoviesResult(Throwable t) {
        this(Movie.EMPTY, Movie.EMPTY, Movie.EMPTY, t);
    }

    private DiscoverMoviesResult(Movie a, Movie b, Movie c, Throwable t) {
        super(t);
        movie1 = a;
        movie2 = b;
        movie3 = c;
    }

    @Override
    public Model updateModel(Model old) {
        return new Model(movie1, movie2, movie3,
                this == LOADING, this == LOADING, this == LOADING,
                old.loadingImdbInfoA, old.loadingImdbInfoB, old.loadingImdbInfoC);
    }

    public static DiscoverMoviesResult failure(Throwable t) {
        return new DiscoverMoviesResult(t);
    }

    public static DiscoverMoviesResult success(List<Movie> movies) {
        return new DiscoverMoviesResult(
                (movies.size() > 0 ? movies.get(0) : Movie.EMPTY),
                (movies.size() > 1 ? movies.get(1) : Movie.EMPTY),
                (movies.size() > 2 ? movies.get(2) : Movie.EMPTY)

        );
    }
}
