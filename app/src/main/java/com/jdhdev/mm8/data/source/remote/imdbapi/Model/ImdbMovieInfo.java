package com.jdhdev.mm8.data.source.remote.imdbapi.Model;


import java.util.Collections;
import java.util.List;


public class ImdbMovieInfo {
    public static final ImdbMovieInfo EMPTY =
            new ImdbMovieInfo("", "", "", Collections.emptyList());
    public final String imdbId;
    public final String rating;
    public final String metaScore;
    public final List<CastMemeber> cast;

    public ImdbMovieInfo(String id,
                         String rating,
                         String metaScore,
                         List<CastMemeber> cast) {
        this.imdbId = id;
        this.rating = rating;
        this.metaScore = metaScore;
        this.cast = cast;
    }

    public String toString() {
        return "{id: " + imdbId + ", rating: " + rating + ", cast: " +
                ((cast != null) ? cast.size() : null) + "}";
    }
}
