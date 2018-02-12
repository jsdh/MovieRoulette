package com.jdhdev.mm8.data.action;

import com.jdhdev.mm8.data.Model;
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo;

import static com.jdhdev.mm8.util.TheMovieDBUtils.equalImdb;


public class GetImdbInfoResult extends Result {
    public final boolean loading;
    public final String imdbId;
    public final ImdbMovieInfo movieInfo;

    public GetImdbInfoResult(ImdbMovieInfo m) {
        this(m.imdbId, m, false);
    }

    public GetImdbInfoResult(String id, boolean inFlight) {
        this(id, null, inFlight);
    }

    public GetImdbInfoResult(String id, ImdbMovieInfo m, boolean inFlight) {
        imdbId = id;
        movieInfo = m;
        loading = inFlight;
    }

    public GetImdbInfoResult(String id, Throwable t) {
        super(t);
        imdbId = id;
        movieInfo = ImdbMovieInfo.EMPTY;
        loading = false;
    }

    @Override
    public Model updateModel(Model old) {
        Model update = old;
        if (loading) {
            update = new Model(old.movieA, old.movieB, old.movieC,
                    old.loadingMovieA, old.loadingMovieB, old.loadingMovieC,
                    equalImdb(imdbId, old.movieA) || old.loadingImdbInfoA,
                    equalImdb(imdbId, old.movieB) || old.loadingImdbInfoB,
                    equalImdb(imdbId, old.movieC) || old.loadingImdbInfoC);

        } else if (equalImdb(imdbId, old.movieA)) {
            update = new Model(old.movieA.updateWithImdbInfo(movieInfo), old.movieB, old.movieC,
                    old.loadingMovieA, old.loadingMovieB, old.loadingMovieC,
                    false, old.loadingImdbInfoB, old.loadingImdbInfoC);

        } else if (equalImdb(imdbId, old.movieB)) {
            update = new Model(old.movieA, old.movieB.updateWithImdbInfo(movieInfo), old.movieC,
                    old.loadingMovieA, old.loadingMovieB, old.loadingMovieC,
                    old.loadingImdbInfoA, false, old.loadingImdbInfoC);

        } else if (equalImdb(imdbId, old.movieC)) {
            update = new Model(old.movieA, old.movieB, old.movieC.updateWithImdbInfo(movieInfo),
                    old.loadingMovieA, old.loadingMovieB, old.loadingMovieC,
                    old.loadingImdbInfoA, old.loadingImdbInfoB, false);
        }

        return update;
    }

    public static GetImdbInfoResult failure(String id, Throwable t) {
        return new GetImdbInfoResult(id, t);
    }

    public static GetImdbInfoResult success(ImdbMovieInfo movie) {
        return new GetImdbInfoResult(movie);
    }

    public static GetImdbInfoResult loading(String id) {
        return new GetImdbInfoResult(id, true);
    }
}
