package com.jdhdev.mm8;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.jdhdev.mm8.di.AppComponent;
import com.jdhdev.mm8.di.DaggerAppComponent;


public class MmApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        appComponent = DaggerAppComponent.builder().application(this).build();
    }

    public AppComponent getObjectGraph() {
        return appComponent;
    }
}
