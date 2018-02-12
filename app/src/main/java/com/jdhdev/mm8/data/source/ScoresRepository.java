package com.jdhdev.mm8.data.source;


import com.jdhdev.mm8.data.Remote;
import com.jdhdev.mm8.data.source.local.ImdbLocalSource;
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo;
import com.jdhdev.mm8.util.RxUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class ScoresRepository implements ScoresDataSource {
    private ScoresDataSource imdbRemoteSource;
    private ScoresDataSource imdbLocalSource;

    @Inject
    public ScoresRepository(@Remote ScoresDataSource remote,
                            @Local ScoresDataSource local) {
        imdbRemoteSource = remote;
        imdbLocalSource = local;
    }

    @Override
    public Observable<ImdbMovieInfo> getScore(String imdbId) {
        return Observable.concat(
                imdbLocalSource.getScore(imdbId)
                        .compose(RxUtils.logSource("SCORES-LOCAL")),
                imdbRemoteSource.getScore(imdbId)
                        .compose(RxUtils.logSource("SCORES-REMOTE"))
                        .doOnNext(this::updateCache))
                .firstElement()
                .toObservable();
    }

    private void updateCache(ImdbMovieInfo rating) {
        // TODO cache not yet implemented
    }
}
