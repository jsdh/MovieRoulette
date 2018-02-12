package com.jdhdev.mm8.data;

import com.jdhdev.mm8.data.action.Action;
import com.jdhdev.mm8.data.action.DiscoverMoviesResult;
import com.jdhdev.mm8.data.action.DiscoverMoviesAction;
import com.jdhdev.mm8.data.action.GetImdbInfoAction;
import com.jdhdev.mm8.data.action.GetImdbInfoResult;
import com.jdhdev.mm8.data.action.GetMovieDetailsAction;
import com.jdhdev.mm8.data.action.GetMovieDetailsResult;
import com.jdhdev.mm8.data.action.Result;
import com.jdhdev.mm8.data.source.MovieDataSource;
import com.jdhdev.mm8.data.source.ScoresDataSource;
import com.jdhdev.mm8.data.source.TheMovieDbConfigSource;
import com.jdhdev.mm8.util.BaseSchedulerProvider;
import com.jdhdev.mm8.util.RxUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class DataManagerImpl implements DataManager {
    private static final Model INITIAL_STATE = new Model();

    private final MovieDataSource moviesRepository;
    private final ScoresDataSource scoresRepository;
    private final TheMovieDbConfigSource configSource;
    private final BaseSchedulerProvider schedulerProvider;

    // Action to Result transformers
    private ObservableTransformer<DiscoverMoviesAction, DiscoverMoviesResult> discover;
    private ObservableTransformer<GetMovieDetailsAction, GetMovieDetailsResult> details;
    private ObservableTransformer<GetImdbInfoAction, GetImdbInfoResult> imdbInfo;

    private BehaviorSubject<Action> actions;
    private Observable<Result> results;
    private Observable<Model> models;

    @Inject
    public DataManagerImpl(MovieDataSource mRepo,
                       ScoresDataSource sRepo,
                       TheMovieDbConfigSource cSource,
                       BaseSchedulerProvider schedulers) {
        moviesRepository = mRepo;
        scoresRepository = sRepo;
        configSource = cSource;
        schedulerProvider = schedulers;

        init();

        models.subscribe(
                model -> { if (model == INITIAL_STATE) findNewRandomMovies(false); });
    }

    private void init() {
        discover = dmAction -> dmAction
                .flatMap(dma -> moviesRepository.getMovies()
                        .map(DiscoverMoviesResult::success)
                        .onErrorReturn(DiscoverMoviesResult::failure)
                        .startWith(DiscoverMoviesResult.LOADING));

        details = gmdAction -> gmdAction
                .flatMap(gmda -> moviesRepository.getMovieDetails(gmda.id)
                        .map(GetMovieDetailsResult::success)
                        .onErrorReturn(t -> GetMovieDetailsResult.failure(gmda.id, t))
                        .startWith(GetMovieDetailsResult.loading(gmda.id)));

        imdbInfo = giiAction -> giiAction
                        .flatMap(giia -> scoresRepository.getScore(giia.imdbId)
                                .map(GetImdbInfoResult::success)
                                .onErrorReturn(t -> GetImdbInfoResult.failure(giia.imdbId, t))
                                .startWith(GetImdbInfoResult.loading(giia.imdbId)));

        actions = BehaviorSubject.create();

        results = actions
                .compose(RxUtils.logSource("Actions-Stream"))
                .publish(shared -> Observable.merge(
                        shared.ofType(DiscoverMoviesAction.class).compose(discover),
                        shared.ofType(GetMovieDetailsAction.class).compose(details),
                        shared.ofType(GetImdbInfoAction.class).compose(imdbInfo)));


        models = results
                .scan(INITIAL_STATE, (state, result) -> {
                    maybeLoadAdditionalDetails(result);
                    return result.updateModel(state);
                })
                .subscribeOn(schedulerProvider.io())
                .replay(1)
                .autoConnect();
    }

    private void maybeLoadAdditionalDetails(Result result) {
        if (result instanceof DiscoverMoviesResult) {
            DiscoverMoviesResult dr = (DiscoverMoviesResult) result;
            if (!dr.hasError() && dr != DiscoverMoviesResult.LOADING) {
                if (dr.movie1 != Movie.EMPTY) getMovieDetails(dr.movie1.id);
                if (dr.movie2 != Movie.EMPTY) getMovieDetails(dr.movie2.id);
                if (dr.movie3 != Movie.EMPTY) getMovieDetails(dr.movie3.id);
            }

        } else if (result instanceof GetMovieDetailsResult) {
                GetMovieDetailsResult mdr = (GetMovieDetailsResult) result;
            if (!mdr.hasError() && !mdr.loading &&
                    mdr.movieDetails != null &&
                    mdr.movieDetails.imdbId != null &&
                    !mdr.movieDetails.imdbId.isEmpty()) {

                getScore(mdr.movieDetails.imdbId);
            }
        }
    }

    @Override
    public Observable<Model> getModelStream() {
        return models;
    }

    private void getScore(String imdbId) {
        actions.onNext(new GetImdbInfoAction(imdbId));
    }

    @Override
    public void findNewRandomMovies(boolean forceUpdate) {
        if (forceUpdate) {
            moviesRepository.refreshMovies()
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(() -> actions.onNext(new DiscoverMoviesAction()));
        } else {
            actions.onNext(new DiscoverMoviesAction());
        }
    }

    @Override
    public Single<TmdbConfiguration> getConfiguration() {
        return configSource.getConfig()
                .subscribeOn(schedulerProvider.io());
    }

    @Override
    public void saveConfiguration(TmdbConfiguration config) {
        configSource.setConfig(config)
                .mergeWith(moviesRepository.resetMovieData())
                .subscribeOn(schedulerProvider.io())
                .subscribe();
    }

    private void getMovieDetails(int id) {
        actions.onNext(new GetMovieDetailsAction(id));
    }

}
