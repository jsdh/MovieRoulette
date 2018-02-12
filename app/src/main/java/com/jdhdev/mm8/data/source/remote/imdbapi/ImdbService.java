package com.jdhdev.mm8.data.source.remote.imdbapi;


import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ImdbService {
    private OkHttpClient client;
    private ImdbResponseParser parser;

    @Inject
    public ImdbService(OkHttpClient httpClient, ImdbResponseParser irp) {
        client = httpClient;
        parser = irp;
    }

    public Observable<ImdbMovieInfo> getImdbInfo(final String mid) {
        return Observable.fromCallable(() -> getImdbInfoHttp(mid));
    }

    private ImdbMovieInfo getImdbInfoHttp(final String id) throws IOException{
        String html = null;

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("m.imdb.com")
                .addPathSegment("title")
                .addPathSegment(id)
                .addPathSegment("")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            if (response.body() != null) html = response.body().string();
        } catch (IOException ioe) {
            System.out.println("failed to get imdb info: " + ioe);
            throw ioe;
        }

        return parser.parseResponse(html);
    }


}
