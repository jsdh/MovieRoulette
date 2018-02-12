package com.jdhdev.mm8.di;


import android.app.Application;

import com.jdhdev.mm8.data.DataManager;
import com.jdhdev.mm8.util.BaseSchedulerProvider;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        RepositoryModule.class,
        ThreadingModule.class})
public interface AppComponent {
    DataManager getDataManager();
    BaseSchedulerProvider getSchedulerProvider();

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
