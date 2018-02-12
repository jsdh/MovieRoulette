package com.jdhdev.mm8.di;

import com.jdhdev.mm8.data.Remote;
import com.jdhdev.mm8.data.source.Local;
import com.jdhdev.mm8.data.source.MovieDataSource;
import com.jdhdev.mm8.data.source.ScoresDataSource;
import com.jdhdev.mm8.data.source.local.ImdbLocalSource;
import com.jdhdev.mm8.data.source.local.TheMovieDBLocalSource;
import com.jdhdev.mm8.data.source.remote.ImdbRemoteSource;
import com.jdhdev.mm8.data.source.remote.TheMovieDBRemoteSource;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;


@Module(includes = {NetworkModule.class, PersistenceModule.class})
public abstract class DataSourceModule {
    @Singleton
    @Binds
    @Remote
    public abstract MovieDataSource provideTMDBRemoteSource(TheMovieDBRemoteSource remoteSource);

    @Binds
    @Local
    public abstract MovieDataSource provideTMDBLocalSource(TheMovieDBLocalSource localSource);

    @Binds
    @Remote
    public abstract ScoresDataSource provideScoresRemoteSource(ImdbRemoteSource remoteSource);

    @Binds
    @Local
    public abstract ScoresDataSource provideScoresLocalSource(ImdbLocalSource localSource);
}
