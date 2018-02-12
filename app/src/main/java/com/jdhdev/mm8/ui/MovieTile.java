package com.jdhdev.mm8.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdhdev.mm8.R;
import com.jdhdev.mm8.data.Movie;
import com.jdhdev.mm8.util.ImageUtils;
import com.jdhdev.mm8.util.TheMovieDBUtils;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.BallScaleIndicator;
import com.wang.avi.indicators.LineScalePulseOutRapidIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieTile extends FrameLayout {

    @BindView(R.id.movie) TextView movieTitle;
    @BindView(R.id.movie_year) TextView movieYr;
    @BindView(R.id.movie_background) ImageView movieBg;
    @BindView(R.id.imdb_logo) ImageView imdbLogo;
    @BindView(R.id.meta_bg) View metaBg;
    @BindView(R.id.movie_imdb_score) TextView movieImdb;
    @BindView(R.id.movie_meta_score) TextView movieMeta;
    @BindView(R.id.loadingMovie) AVLoadingIndicatorView loadingMovie;
    @BindView(R.id.loadingScores) AVLoadingIndicatorView loadingScores;

    private Movie currentMovie;

    public MovieTile(@NonNull Context context) {
        super(context);
        initializeViews(context);
    }

    public MovieTile(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MovieTile(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public MovieTile(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context);
    }

    public TextView getTitleView() {
        return movieTitle;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.movie_tile_view, this);
    }

    public void bind(Movie movie) {
        currentMovie = movie;

        movieTitle.setText(movie.title);
        movieYr.setText(movie.getReleaseYear());

        movieImdb.setText(movie.imdbScore);
        boolean hasImdbScore = !TextUtils.isEmpty(movie.imdbScore);
        imdbLogo.setVisibility(hasImdbScore ? VISIBLE : INVISIBLE);
        movieImdb.setVisibility(hasImdbScore ? VISIBLE : INVISIBLE);

        movieMeta.setText(movie.metaScore);
        boolean hasMetaScore = !TextUtils.isEmpty(movie.metaScore);
        metaBg.setVisibility(hasMetaScore ? VISIBLE : INVISIBLE);
        movieMeta.setVisibility(hasMetaScore ? VISIBLE : INVISIBLE);
        if (hasMetaScore)
            metaBg.setBackground( new ColorDrawable(
                    TheMovieDBUtils.colorForMetaScore(getContext(), movie.metaScore)));

        String bgUri = movie.getBgImage();
        if (!TextUtils.isEmpty(bgUri)) {
            ImageUtils.loadImageIntoView(getContext(),
                    movieBg,
                    bgUri);
        } else {
            movieBg.setImageDrawable(null);
        }
    }

    public void setLoadingMovie(boolean isLoading) {
        loadingMovie.setVisibility(isLoading ? VISIBLE : GONE);
    }

    public void setLoadingScores(boolean isLoading) {
        loadingScores.setVisibility(isLoading ? VISIBLE : GONE);
    }


    public Movie getMovie() {
        return currentMovie;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        loadingMovie.setIndicator(new LineScalePulseOutRapidIndicator());
        loadingScores.setIndicator(new BallScaleIndicator());
    }
}
