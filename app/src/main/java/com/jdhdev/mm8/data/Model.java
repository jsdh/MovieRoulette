package com.jdhdev.mm8.data;


public class Model {
    public final Movie movieA;
    public final Movie movieB;
    public final Movie movieC;
    public final boolean loadingMovieA;
    public final boolean loadingMovieB;
    public final boolean loadingMovieC;
    public final boolean loadingImdbInfoA;
    public final boolean loadingImdbInfoB;
    public final boolean loadingImdbInfoC;

    public Model() {
        this(Movie.EMPTY, Movie.EMPTY, Movie.EMPTY,
                false, false, false,
                false, false, false);
    }

    public Model(Movie mA, Movie mB, Movie mC,
                 boolean loadingMA, boolean loadingMB, boolean loadingMC,
                 boolean loadingImdbA, boolean loadingImdbB, boolean loadingImdbC) {
        movieA = mA;
        movieB = mB;
        movieC = mC;
        loadingMovieA = loadingMA;
        loadingMovieB = loadingMB;
        loadingMovieC = loadingMC;
        loadingImdbInfoA = loadingImdbA;
        loadingImdbInfoB = loadingImdbB;
        loadingImdbInfoC = loadingImdbC;
    }

    public Movie getMovieWithId(int id) {
        if (id == movieA.id) return movieA;
        if (id == movieB.id) return movieB;
        if (id == movieC.id) return movieC;
        return Movie.EMPTY;
    }

    public String toString() {
        return String.format("{mA:%s, mB:%s, mC:%s, loadingA:%s, loadingB:%s, loadingC:%s, imdbA:%s, imdbB:%s imdbC:%s}",
                movieA.id, movieB.id, movieC.id,
                loadingMovieA, loadingMovieB, loadingMovieC,
                loadingImdbInfoA, loadingImdbInfoB, loadingImdbInfoC);
    }
}
