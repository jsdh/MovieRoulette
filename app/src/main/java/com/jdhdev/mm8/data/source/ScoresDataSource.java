package com.jdhdev.mm8.data.source;

import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo;

import io.reactivex.Observable;


public interface ScoresDataSource {
    Observable<ImdbMovieInfo> getScore(String id);
}
