package com.jdhdev.mm8.di;

import com.jdhdev.mm8.data.DataManager;
import com.jdhdev.mm8.data.FakeDataManager;
import com.jdhdev.mm8.data.source.MovieDataSource;
import com.jdhdev.mm8.data.source.MoviesRepository;
import com.jdhdev.mm8.data.source.ScoresDataSource;
import com.jdhdev.mm8.data.source.ScoresRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;


@Module(includes = DataSourceModule.class)
public abstract class RepositoryModule {
    @Binds
    public abstract MovieDataSource provideMovieRepository(MoviesRepository moviesRepository);

    @Binds
    public abstract ScoresDataSource provideScoresRepository(ScoresRepository scoresRepository);

    @Singleton
    @Binds
    public abstract DataManager provideDataManager(FakeDataManager fakeDataManager);
}
