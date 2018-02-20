package com.jdhdev.mm8.movieDetails;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdhdev.mm8.R;
import com.jdhdev.mm8.data.Movie;
import com.jdhdev.mm8.ui.ListItemDivider;
import com.jdhdev.mm8.util.ImageUtils;
import com.jdhdev.mm8.util.TheMovieDBUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieDetailsFragment extends Fragment implements MovieDetailsContract.View {

    @BindView(R.id.header_image) ImageView headerPic;
    @BindView(R.id.release_date) TextView releaseDate;
    @BindView(R.id.esrb_rating) TextView esrbRating;
    @BindView(R.id.languages) TextView languague;
    @BindView(R.id.runtime) TextView runtime;
    @BindView(R.id.genres) TextView genres;
    @BindView(R.id.imdb_logo) ImageView imdbLogo;
    @BindView(R.id.imdb_rating) TextView imdbRating;
    @BindView(R.id.metascore_bg) View metaBg;
    @BindView(R.id.metascore) TextView metascore;
    @BindView(R.id.metascore_container) View metascoreContainer;
    @BindView(R.id.metascore_title) TextView metascoreTitle;
    @BindView(R.id.poster) ImageView poster;
    @BindView(R.id.summary) TextView summary;
    @BindView(R.id.fab) FloatingActionButton favorite;
    @BindView(R.id.cast_list) RecyclerView castList;
    @BindView(R.id.image_list) RecyclerView imageList;
    @BindView(R.id.share_movie) ImageView share;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.title_transition_dummy) TextView titleDummy;

    private MovieDetailsContract.Presenter presenter;
    private CastListAdapter castListAdapter;
    private ImageListAdapter imageListAdapter;
    private Drawable favoriteIcon;
    private double collapsedPercent = 0;

    public MovieDetailsFragment() {}

    public static MovieDetailsFragment newInstance() {
        return new MovieDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, root);

        share.setOnClickListener(v -> share());
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar((Toolbar) root.findViewById(R.id.toolbar));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        favoriteIcon = activity.getDrawable(R.drawable.ic_not_favorite);
        favoriteIcon.setColorFilter(getResources().getColor(R.color.favorite_icon_default), PorterDuff.Mode.SRC_IN);
        favorite.setImageDrawable(favoriteIcon);
        favorite.setOnClickListener(v -> addFavorite());
        favorite.setScaleX(0);
        favorite.setScaleY(0);

        Bundle args = getArguments();
        if (args != null) {
            titleDummy.setText(args.getString(MovieDetailsActivity.EXTRA_TRANSITION_TITLE_STRING));
        }

        collapsingToolbarLayout.setTitleEnabled(false);

        castList.setHasFixedSize(true);
        castList.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));
        castList.setNestedScrollingEnabled(false);

        ListItemDivider divider = new ListItemDivider(getContext(), R.dimen.border);
        castList.addItemDecoration(divider);

        castListAdapter = new CastListAdapter(
                getActivity().getApplicationContext(), new ArrayList<>());

        castList.setAdapter(castListAdapter);


        imageList.setHasFixedSize(false);
        imageList.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));
        imageList.setNestedScrollingEnabled(false);
        imageList.addItemDecoration(divider);

        imageListAdapter = new ImageListAdapter(
                getActivity().getApplicationContext(), new ArrayList<>());
        imageList.setAdapter(imageListAdapter);

        headerPic.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                headerPic.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().startPostponedEnterTransition();
                return true;
            }
        });

        appBarLayout.addOnOffsetChangedListener((layout, offset) -> {
            int total = collapsingToolbarLayout.getHeight() - toolbar.getHeight();
            collapsedPercent = Math.abs(offset) / (double) total;
        });

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        presenter.unsubscribe();
        super.onPause();
    }

    public void share() {
        presenter.share();
    }

    public void addFavorite() {
        presenter.addFavorite();
    }

    // TODO these should be part of an interface
    public void onEnterTransitionEnd() {
        favorite.setVisibility(View.VISIBLE);
        favorite.setScaleX(0);
        favorite.setScaleY(0);
        favorite.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(210)
                .start();
    }

    public void onExit() {
        favorite.setVisibility(View.GONE);
    }

    public boolean showingBackdrop() {
        return collapsedPercent < 0.8;
    }


    // contract________________

    @Override
    public void setPresenter(MovieDetailsContract.Presenter p) {
        presenter = p;
    }

    @Override
    public void bind(Movie movie) {
        titleDummy.setText(movie.title);
        releaseDate.setText(movie.releaseDate);
        runtime.setText(movie.runtimeForUi());
        genres.setText(movie.genresToString());
        esrbRating.setText(movie.esrbRating);
        languague.setText(movie.language);
        summary.setText(movie.overview);
        setImdbInfo(movie);
        setMetascoreInfo(movie);

        String bgUri = movie.getBgImage();
        if (!TextUtils.isEmpty(bgUri)) {
            ImageUtils.loadImageIntoView(getContext(),
                    headerPic,
                    movie.getBgImage(),
                    () -> {
                        Bitmap bitmap = ((BitmapDrawable) headerPic.getDrawable()).getBitmap();
                        Palette.from(bitmap)
                                .generate(this::applyPalette);
                    });
        } else {
            // TODO add a default image or drawable for when no background image is provided
        }

        String posterUri = movie.posterPath;
        if (!TextUtils.isEmpty(posterUri)) {
            ImageUtils.loadImageIntoView(getContext(),
                    poster,
                    movie.posterPath);
        }

        if (movie.castList != null) {
            castListAdapter.setData(movie.castList);
        } else {
            // TODO -- hide cast list ui
        }

        if (movie.images != null) {
            imageListAdapter.setData(movie.images);
        } else {
            // TODO -- hide images
        }
    }

    private void setImdbInfo(Movie movie) {
        boolean exists = !TextUtils.isEmpty(movie.imdbScore);
        imdbRating.setText(movie.imdbScore);
        imdbLogo.setVisibility(exists ? View.VISIBLE : View.INVISIBLE);
        imdbRating.setVisibility(exists ? View.VISIBLE : View.INVISIBLE);
    }

    private void setMetascoreInfo(Movie movie) {
        boolean exists = !TextUtils.isEmpty(movie.metaScore);
        if (exists) {
            metascore.setText(movie.metaScore);
            metaBg.setBackground(
                    new ColorDrawable(
                            TheMovieDBUtils.colorForMetaScore(getContext(), movie.metaScore)));
        }
        metaBg.setVisibility(exists ? View.VISIBLE : View.INVISIBLE);
        metascore.setVisibility(exists ? View.VISIBLE : View.INVISIBLE);
        metascoreTitle.setVisibility(exists ? View.VISIBLE : View.INVISIBLE);
    }

    private void applyPalette(Palette palette) {
        if (!isAdded()) return;

        int muted = palette.getMutedColor(getResources().getColor(R.color.colorPrimary));
        int mutedDark = palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark));
        int vibrant = palette.getVibrantColor(getResources().getColor(R.color.favorite_bg_default));
        int lightVibrant = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int darkVibrant = palette.getDarkVibrantColor(getResources().getColor(R.color.favorite_icon_default));

        collapsingToolbarLayout.setContentScrimColor(muted);
        collapsingToolbarLayout.setStatusBarScrimColor(mutedDark);

        favorite.setRippleColor(lightVibrant);
        favorite.setBackgroundTintList(ColorStateList.valueOf(vibrant));
        favoriteIcon.setColorFilter(darkVibrant, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void showMissingDetails() {

    }

    @Override
    public void setLoadingIndicator(boolean loading) {

    }

    @Override
    public void showToast(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT)
                .show();
    }


}
