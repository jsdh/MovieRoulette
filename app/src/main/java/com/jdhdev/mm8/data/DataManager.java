package com.jdhdev.mm8.data;


import io.reactivex.Observable;
import io.reactivex.Single;

public interface DataManager {

    Observable<Model> getModelStream();
    Single<TmdbConfiguration> getConfiguration();
    void findNewRandomMovies(boolean forceUpdate);
    void saveConfiguration(TmdbConfiguration config);
}
