package com.jdhdev.mm8.di;


import com.jdhdev.mm8.util.BaseSchedulerProvider;
import com.jdhdev.mm8.util.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ThreadingModule {
    @Binds
    public abstract BaseSchedulerProvider provideScheduler(SchedulerProvider schedulerProvider);
}
