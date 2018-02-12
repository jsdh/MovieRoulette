package com.jdhdev.mm8.data.source.remote.themoviedbapi;

import com.jdhdev.mm8.BuildConfig;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.DiscoverResult;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.MovieDetails;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface MovieDBService {
    String ENDPOINT = "https://api.themoviedb.org/3/";

    @GET("discover/movie?api_key=" + BuildConfig.TMDB_API_KEY + "&language=en-US&sort_by=popularity.desc&certification_country=US")
    Observable<DiscoverResult> discoverMovies(@Query("page") int page,
                                              @Query("certification.lte") String maxRating,
                                              @Query("with_genres") String genres,
                                              @Query("include_adult") boolean includeAdult,
                                              @Query("release_date.gte")int minYear,
                                              @Query("release_date.lte")int maxYear);

    @GET("movie/{movie_id}?api_key=" + BuildConfig.TMDB_API_KEY + "&append_to_response=videos,images,release_dates")
    Observable<MovieDetails> getMovieDetails(@Path("movie_id") int mId);

}
