package com.jdhdev.mm8.data.source.remote;

import com.jdhdev.mm8.data.source.ScoresDataSource;
import com.jdhdev.mm8.data.source.remote.imdbapi.ImdbService;
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


public class ImdbRemoteSource implements ScoresDataSource {
    private ImdbService service;

    @Inject
    public ImdbRemoteSource(ImdbService imdbService) {
        service = imdbService;
    }

    @Override
    public Observable<ImdbMovieInfo> getScore(String imdbId) {
        return service.getImdbInfo(imdbId)
                .subscribeOn(Schedulers.io());
    }
}
