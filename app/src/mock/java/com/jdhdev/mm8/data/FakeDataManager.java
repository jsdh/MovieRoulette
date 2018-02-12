package com.jdhdev.mm8.data;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

public class FakeDataManager implements DataManager {
    public static BehaviorSubject<Model> testSource;
    public static Single<TmdbConfiguration> config;
    public static int findNewRandomMoviesClicks;

    public static TmdbConfiguration lastSavedConfig;

    static {
        testSource = BehaviorSubject.create();
        findNewRandomMoviesClicks = 0;
        config = Single.just(new TmdbConfiguration());
    }

    @Inject
    public FakeDataManager()  {}

    @Override
    public Observable<Model> getModelStream() {
        return testSource;
    }

    @Override
    public Single<TmdbConfiguration> getConfiguration() {
        return config;
    }

    @Override
    public void findNewRandomMovies(boolean forceUpdate) {
        findNewRandomMoviesClicks++;
    }

    @Override
    public void saveConfiguration(TmdbConfiguration c) {
        lastSavedConfig = c;
    }
}