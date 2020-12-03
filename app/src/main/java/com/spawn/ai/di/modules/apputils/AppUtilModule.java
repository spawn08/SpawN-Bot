package com.spawn.ai.di.modules.apputils;

import com.spawn.ai.utils.task_utils.AppUtils;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
@InstallIn(ApplicationComponent.class)
public class AppUtilModule {

    @Provides
    @Singleton
    @Named("ForAppUtils")
    public AppUtils provideAppUtils() {
        return new AppUtils();
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable(){
        return new CompositeDisposable();
    }
}
