package com.jdhdev.mm8.data.source.local;

import com.jdhdev.mm8.data.source.ScoresDataSource;
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo;

import javax.inject.Inject;

import io.reactivex.Observable;


public class ImdbLocalSource implements ScoresDataSource {
    @Inject
    public ImdbLocalSource() {

    }

    @Override
    public Observable<ImdbMovieInfo> getScore(String id) {
        return Observable.empty();
    }

    public void addCache(String id, ImdbMovieInfo rating) {
        // TODO
    }
}
