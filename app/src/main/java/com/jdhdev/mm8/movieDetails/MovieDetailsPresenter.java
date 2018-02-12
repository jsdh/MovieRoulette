package com.jdhdev.mm8.movieDetails;


import com.jdhdev.mm8.data.Model;
import com.jdhdev.mm8.data.Movie;
import com.jdhdev.mm8.data.DataManager;
import com.jdhdev.mm8.movies.MoviesContract;
import com.jdhdev.mm8.util.BaseSchedulerProvider;
import com.jdhdev.mm8.util.RxUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class MovieDetailsPresenter implements MovieDetailsContract.Presenter {
    private final int movieSlot;
    private final DataManager dataManager;
    private final MovieDetailsContract.View detailsView;
    private final BaseSchedulerProvider schedulerProvider;

    private Disposable modelSub;

    public MovieDetailsPresenter(int id, BaseSchedulerProvider scheduler,
                                 DataManager dm, MovieDetailsContract.View view) {
        movieSlot = id;
        dataManager = dm;
        detailsView = view;
        schedulerProvider = scheduler;

        detailsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (modelSub != null) {
            modelSub.dispose();
            throw new IllegalStateException("MDP called sub with active model subscription");
        }

        modelSub = dataManager.getModelStream()
                .compose(RxUtils.logSource("MDP::UI-Model"))
                .observeOn(schedulerProvider.ui())
                .subscribe(this::showMovieDetails); // TODO -- handle error

    }

    @Override
    public void unsubscribe() {
        modelSub.dispose();
        modelSub = null;
    }

    @Override
    public void share() {
        detailsView.showToast("This feature is not supported yet.. coming soon");
    }

    @Override
    public void addFavorite() {
        detailsView.showToast("This feature is not supported yet.. coming soon");
    }

    private void showMovieDetails(Model model) {
        Movie movie = Movie.EMPTY;
        if (movieSlot == MoviesContract.View.MOVIE_SLOT_1) movie = model.movieA;
        if (movieSlot == MoviesContract.View.MOVIE_SLOT_2) movie = model.movieB;
        if (movieSlot == MoviesContract.View.MOVIE_SLOT_3) movie = model.movieC;

        if (movie != Movie.EMPTY) {
            detailsView.bind(movie);
        } else {
            detailsView.showMissingDetails();
        }
    }
}
