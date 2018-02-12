package com.jdhdev.mm8.data.source.remote.themoviedbapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DiscoverMovie {
    @SerializedName("poster_path")
    public String posterPath;
    public boolean adult;
    public String overview;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("genre_ids")
    public List<Integer> genreIds = null;
    public int id;
    @SerializedName("original_title")
    public String originalTitle;
    @SerializedName("original_language")
    public String originalLanguage;
    public String title;
    @SerializedName("backdrop_path")
    public String backdropPath;
    public double popularity;
    @SerializedName("vote_count")
    public int voteCount;
    public boolean video;
    @SerializedName("vote_average")
    public double voteAverage;

    public String toString() {
        return String.format("{title: %s, Id: %s, originalTitle: %s, posterPath: %s, " +
                "backdrop: %s, adult: %b, genreIds: %s}",
                title,
                id,
                originalTitle,
                posterPath,
                backdropPath,
                adult,
                genreIds);
    }
}
