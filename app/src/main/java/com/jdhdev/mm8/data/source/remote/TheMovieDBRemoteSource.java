package com.jdhdev.mm8.data.source.remote;

import com.jdhdev.mm8.data.Movie;
import com.jdhdev.mm8.data.TmdbConfiguration;
import com.jdhdev.mm8.data.source.MovieDataSource;
import com.jdhdev.mm8.data.source.TheMovieDbConfigSource;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.MovieDBService;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.DiscoverMovie;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.DiscoverResult;
import static com.jdhdev.mm8.util.RxUtils.logSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class TheMovieDBRemoteSource implements MovieDataSource {
    private MovieDBService movieDBService;
    private TheMovieDbConfigSource movieDbConfigSource;
    private int maxPage;
    private Random random;

    private static class DiscoverRequestParams {
        int maxPage;
        TmdbConfiguration config;
        DiscoverRequestParams(int p, TmdbConfiguration c) {
            maxPage = p;
            config = c;
        }
    }

    @Inject
    public TheMovieDBRemoteSource(OkHttpClient client, TheMovieDbConfigSource configSource) {
        movieDbConfigSource = configSource;
        // TODO retrofit dependency needs to be externalized so we can unit test this class...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDBService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        movieDBService = retrofit.create(MovieDBService.class);
        random = new Random(System.currentTimeMillis());
        maxPage = -1;
    }

    private Observable<Integer> getMaxPage() {
        return Observable.concat(
                    Observable.just(maxPage)
                        .compose(logSource("MaxPageCache")),
                    getMaxPageFromNetwork())
                .filter(page -> page >= 1)
                .first(1)
                .toObservable();
    }

    private Observable<Integer> getMaxPageFromNetwork() {
        // TODO -- 'getConfigParams gets called twice, what about using pusblish w/ params like
        //         with the UI model stuff??
        return Observable.zip(Observable.just(1), getConfigParams(), DiscoverRequestParams::new)
                .flatMap(this::discoverNewMovies)
                .map(response -> updateMaxPage(response.totalPages))
                .compose(logSource("MaxPageNetwork"));
    }

    private int updateMaxPage(int max) {
        // Tmdb API has an upper limit of 1000, even though the API sometimes returns a value greater than that
        maxPage = Math.min(Math.max(1, max), 1000);
        return maxPage;
    }

    private Observable<TmdbConfiguration> getConfigParams() {
        return movieDbConfigSource.getConfig().toObservable();
    }

    private Observable<DiscoverResult> discoverNewMovies(DiscoverRequestParams params) {
        return movieDBService.discoverMovies(random.nextInt(params.maxPage) + 1,
                params.config.getRating().getStringValue(),
                params.config.getGenreValues(),
                params.config.getIncludeAdult(),
                params.config.getStartYear(),
                params.config.getEndYear());
    }

    @Override
    public Observable<List<Movie>> getMovies() {
        return Observable.zip(getMaxPage(), getConfigParams(), DiscoverRequestParams::new)
                .flatMap(this::discoverNewMovies)
                .compose(logSource("TheMovieDBApi"))
                .map(this::DiscoverMovieToMovieList);
    }

    @Override
    public Observable<Movie> getMovieDetails(int id) {
        return movieDBService.getMovieDetails(id)
                .map(Movie::from);
    }

    private List<Movie> DiscoverMovieToMovieList(DiscoverResult result) {
        Collections.shuffle(result.results);
        List<Movie> movieList = new ArrayList<>();
        for (DiscoverMovie dm : result.results) {
            movieList.add(Movie.from(dm));
        }

        return movieList;
    }

    @Override
    public Completable refreshMovies() {
        return Completable.complete();
    }

    @Override
    public Completable resetMovieData() {
        maxPage = -1;

        return Completable.complete();
    }
}
