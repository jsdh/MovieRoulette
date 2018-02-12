package com.jdhdev.mm8.data.action;


import com.jdhdev.mm8.data.Model;
import com.jdhdev.mm8.data.Movie;

public class GetMovieDetailsResult extends Result {
    public final boolean loading;
    public final int id;
    public final Movie movieDetails;


    public GetMovieDetailsResult(Movie details) {
        this(details.id, details, false);
    }

    public GetMovieDetailsResult(int movieId, Throwable t) {
        super(t);
        id = movieId;
        movieDetails = Movie.EMPTY;
        loading = false;
    }

    public GetMovieDetailsResult(int mId, boolean inFlight) {
        this(mId, null, inFlight);
    }

    public GetMovieDetailsResult(int mId, Movie movie, boolean inFlight) {
        id = mId;
        movieDetails = movie;
        loading = inFlight;
    }

    @Override
    public Model updateModel(Model model) {
        if (loading) {
            return new Model(model.movieA, model.movieB, model.movieC,
                    id == model.movieA.id || model.loadingMovieA,
                    id == model.movieB.id || model.loadingMovieB,
                    id == model.movieC.id || model.loadingMovieC,
                    model.loadingImdbInfoA, model.loadingImdbInfoB, model.loadingImdbInfoC);
        }

        boolean m1Matches = model.movieA.id == id;
        boolean m2Matches = model.movieB.id == id;
        boolean m3Matches = model.movieC.id == id;

        if (m1Matches) {
            model = new Model(getUpdatedMovie(model.movieA), model.movieB, model.movieC,
                    false, model.loadingMovieB, model.loadingMovieC,
                    model.loadingImdbInfoA, model.loadingImdbInfoB, model.loadingImdbInfoC);
        }
        if (m2Matches) {
            model = new Model(model.movieA, getUpdatedMovie(model.movieB), model.movieC,
                    model.loadingMovieA, false, model.loadingMovieC,
                    model.loadingImdbInfoA, model.loadingImdbInfoB, model.loadingImdbInfoC);
        }
        if (m3Matches) {
            model = new Model(model.movieA, model.movieB, getUpdatedMovie(model.movieC),
                    model.loadingMovieA, model.loadingMovieB, false,
                    model.loadingImdbInfoA, model.loadingImdbInfoB, model.loadingImdbInfoC);
        }

        return model;
    }

    private Movie getUpdatedMovie(Movie old) {
        if (!hasError()) return movieDetails;
        return old;
    }

    public static GetMovieDetailsResult failure(int id, Throwable t) {
        return new GetMovieDetailsResult(id, t);
    }

    public static GetMovieDetailsResult success(Movie m) {
        return new GetMovieDetailsResult(m);
    }

    public static GetMovieDetailsResult loading(int id) {
        return new GetMovieDetailsResult(id, true);
    }
}
