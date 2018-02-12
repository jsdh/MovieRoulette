package com.jdhdev.mm8.di;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jdhdev.mm8.data.source.remote.imdbapi.ImdbResponseParser;
import com.jdhdev.mm8.data.source.remote.imdbapi.ImdbService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class NetworkModule {
    @Provides
    @Singleton
    public OkHttpClient provideHttpClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }
}
