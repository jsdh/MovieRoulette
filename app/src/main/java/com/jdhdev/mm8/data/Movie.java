package com.jdhdev.mm8.data;

import com.jdhdev.mm8.data.source.local.database.entity.SavedDiscoverMovie;
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.CastMemeber;
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.DiscoverMovie;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.Genre;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.MovieDetails;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.ProductionCompany;
import com.jdhdev.mm8.util.TheMovieDBUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Movie {
    public static final Movie EMPTY = new Movie(-1, "", "", "", "");

    public int id;
    public String title;
    public String backdrop;
    public String posterPath;
    public String releaseDate;
    public String imdbId;
    public boolean adult;
    public int budget;
    public List<String> genres;
    public String homepage;
    public String overview;
    public List<ProductionCompany> productionCompanies;
    public int revenue;
    public int runtime;
    public String language;
    public String status;
    public String tagline;
    public String esrbRating;
    public Set<String> images;
    public Set<String> videos;
    public String imdbScore;
    public String metaScore;

    public List<CastMemeber> castList;

    public static Movie from(MovieDetails details) {
        return new Movie(details.id,
                details.title,
                TheMovieDBUtils.parseReleaseDate(details.releaseDate),
                TheMovieDBUtils.getFullImagePath(details.backdropPath),
                TheMovieDBUtils.getFullImagePath(details.posterPath),
                details.imdbId,
                "",
                "",
                details.adult,
                details.budget,
                getGenreNames(details.genres),
                details.homepage,
                details.overview,
                details.productionCompanies,
                details.revenue,
                details.runtime,
                details.getSpokenLang(),
                details.status,
                details.tagline,
                TheMovieDBUtils.getEsrbRating(details.releaseDates),
                TheMovieDBUtils.getImageUrisList(details.images),
                TheMovieDBUtils.getVideoUrisList(details.videos),
                Collections.emptyList());
    }

    public static Movie from(DiscoverMovie dm) {
        return new Movie(
                dm.id,
                dm.title,
                TheMovieDBUtils.parseReleaseDate(dm.releaseDate),
                TheMovieDBUtils.getFullImagePath(dm.backdropPath),
                TheMovieDBUtils.getFullImagePath(dm.posterPath));
    }

    public static Movie from(SavedDiscoverMovie sdm) {
        return new Movie(
                sdm.getTmdbId(),
                sdm.getTitle(),
                sdm.getReleaseDate(),
                sdm.getBackdropPath(),
                sdm.getPosterPath());
    }


    private static List<String> getGenreNames(List<Genre> genres) {
        List<String> res = new ArrayList<>(genres.size());
        for (Genre g : genres) res.add(g.name);

        return res;
    }

    public Movie(int id, String t, String date, String bg, String poster) {
        this(id, t, date, bg, poster,
                "",
                "",
                "",
                false,
                0,
                Collections.emptyList(),
                "",
                "",
                Collections.emptyList(),
                0,
                0,
                "",
                "",
                "",
                "",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
    }

    public Movie(int id,
                 String title,
                 String releaseDate,
                 String backdrop,
                 String posterPath,
                 String imdbId,
                 String imdbScore,
                 String metacriticScore,
                 boolean adult,
                 int budget,
                 List<String> genres,
                 String homepage,
                 String overview,
                 List<ProductionCompany> productionCompanies,
                 int revenue,
                 int runtime,
                 String language,
                 String status,
                 String tagline,
                 String esrbRating,
                 Collection<String> images,
                 Collection<String> videos,
                 List<CastMemeber> castList) {
        this.id = id;
        this.title = title;
        this.backdrop = backdrop;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.imdbId = imdbId;
        this.imdbScore = imdbScore;
        this.metaScore = metacriticScore;
        this.adult = adult;
        this.budget = budget;
        this.genres = genres;
        this.homepage = homepage;
        this.overview = overview;
        this.productionCompanies = productionCompanies;
        this.revenue =revenue;
        this.runtime = runtime;
        this.language = language;
        this.status = status;
        this.tagline = tagline;
        this.esrbRating = esrbRating;
        this.images = new HashSet<>();
        this.videos = new HashSet<>();
        this.castList = castList;

        if (images != null) this.images.addAll(images);
        if (videos != null) this.videos.addAll(videos);
        if (backdrop != null) this.images.remove(backdrop);
    }

    public void setCastList(List<CastMemeber> clist) {
        castList = clist;
    }

    public String getBgImage() {
        return backdrop != null ? backdrop : posterPath;
    }

    public String genresToString() {
        if (genres == null || genres.size() == 0) return "";

        StringBuilder sb = new StringBuilder();
        for (String genre : genres) {
            sb.append(", ").append(genre);
        }

        return sb.substring(2);
    }

    public String runtimeForUi() {
        int hours = runtime / 60;
        int minutes = runtime % 60;

        if (hours > 0) return hours + "h " + minutes + "mins";
        return minutes + "mins";
    }

    public String getReleaseYear() {
        if (releaseDate != null & releaseDate.length() > 0) {
            int index = releaseDate.lastIndexOf(" ");
            if (index > 0) {
                return releaseDate.substring(index + 1);
            }
        }

        return "";
    }

    public Movie updateWithImdbInfo(ImdbMovieInfo info) {
        return new Movie(id,
                title,
                releaseDate,
                backdrop,
                posterPath,
                imdbId,
                info.rating,
                info.metaScore,
                adult,
                budget,
                genres,
                homepage,
                overview,
                productionCompanies,
                revenue,
                runtime,
                language,
                status,
                tagline,
                esrbRating,
                images,
                videos,
                info.cast);
    }


    public String toString() {
        return "[id=" + id + ", title=" + title +
                " imdbID: " + imdbId +
                ", imdbScore: " + imdbScore + ", releaseDate=" + releaseDate +
                ", runtime=" + runtime + "]";
    }
}
