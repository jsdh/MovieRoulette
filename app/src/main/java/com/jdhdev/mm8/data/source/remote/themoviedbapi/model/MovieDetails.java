package com.jdhdev.mm8.data.source.remote.themoviedbapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetails {
    public boolean adult;
    @SerializedName("backdrop_path")
    public String backdropPath;
    public int budget;
    public List<Genre> genres = null;
    public String homepage;
    public int id;
    @SerializedName("imdb_id")
    public String imdbId;
    @SerializedName("original_language")
    public String originalLanguage;
    @SerializedName("original_title")
    public String originalTitle;
    public String overview;
    public double popularity;
    @SerializedName("poster_path")
    public String posterPath;
    @SerializedName("production_companies")
    public List<ProductionCompany> productionCompanies = null;
    @SerializedName("release_date")
    public String releaseDate;
    public int revenue;
    public int runtime;
    @SerializedName("spoken_languages")
    public List<SpokenLanguage> spokenLanguages = null;
    public String status;
    public String tagline;
    public String title;
    public boolean video;
    @SerializedName("vote_average")
    public double voteAverage;
    @SerializedName("vote_count")
    public int voteCount;
    public VideoList videos;
    public ImageList images;
    @SerializedName("release_dates")
    public ReleaseDateList releaseDates;

    public String getSpokenLang() {
        if (spokenLanguages == null || spokenLanguages.size() == 0) return "";
        return spokenLanguages.get(0).name;
    }
}
