package com.spawn.ai.di.modules.apputils;

import com.spawn.ai.utils.task_utils.AppUtils;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class AppUtilModule {

    @Provides
    @Singleton
    @Named("ForAppUtils")
    public AppUtils provideAppUtils() {
        return new AppUtils();
    }
}
