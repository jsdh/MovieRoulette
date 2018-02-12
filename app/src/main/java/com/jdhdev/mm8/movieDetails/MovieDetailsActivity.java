package com.jdhdev.mm8.movieDetails;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;

import com.jdhdev.mm8.MmApplication;
import com.jdhdev.mm8.R;
import com.jdhdev.mm8.di.AppComponent;
import com.jdhdev.mm8.util.TransitionAction;

import org.jetbrains.annotations.Nullable;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE_ID = "MOVIE_ID";
    public static final String EXTRA_TRANSITION_TITLE_STRING = "TRANSITION_TITLE_STRING";

    private MovieDetailsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        int movieSlot = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);

        fragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = MovieDetailsFragment.newInstance();
            Bundle args = new Bundle();
            args.putString(EXTRA_TRANSITION_TITLE_STRING,
                    getIntent().getStringExtra(EXTRA_TRANSITION_TITLE_STRING));
            fragment.setArguments(args);
            // todo movie to a util class
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragment);
            transaction.commit();
        }

        MmApplication app = (MmApplication) getApplication();
        AppComponent provider = app.getObjectGraph();
        new MovieDetailsPresenter(movieSlot,
                provider.getSchedulerProvider(),
                provider.getDataManager(),
                fragment);


        getWindow().getSharedElementEnterTransition()
                .addListener(new TransitionAction() {
                    @Override
                    public void onTransitionEnd(@Nullable Transition p0) {
                        fragment.onEnterTransitionEnd();
                    }
                });

        //postponeEnterTransition();
    }

    @Override
    public void onBackPressed() {
        fragment.onExit();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
