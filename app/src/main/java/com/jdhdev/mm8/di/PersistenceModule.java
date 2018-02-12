package com.jdhdev.mm8.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.jdhdev.mm8.data.source.local.database.AppDatabase;
import com.jdhdev.mm8.data.source.local.database.TheMovieDbDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class PersistenceModule {
    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(Context context) {
        return Room.databaseBuilder(
                context, AppDatabase.class, AppDatabase.DB_NAME)
                .addCallback(AppDatabase.Companion)
                .build();
    }

    @Provides
    public TheMovieDbDao provideTMDbDao(AppDatabase db) {
        return db.getTMDbConfigDao();
    }
}
