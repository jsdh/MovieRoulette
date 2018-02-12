package com.jdhdev.mm8.movieDetails;


import com.jdhdev.mm8.BasePresenter;
import com.jdhdev.mm8.BaseView;
import com.jdhdev.mm8.data.Movie;

public interface MovieDetailsContract {
    interface View extends BaseView<Presenter>{
        void showMissingDetails();
        void setLoadingIndicator(boolean loading);
        void bind(Movie movie);
        void showToast(String message);
    }

    interface Presenter extends BasePresenter {
        void share();
        void addFavorite();
    }
}
