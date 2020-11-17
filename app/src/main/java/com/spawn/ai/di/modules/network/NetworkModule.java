package com.spawn.ai.di.modules.network;

import com.spawn.ai.BuildConfig;
import com.spawn.ai.interfaces.AzureService;
import com.spawn.ai.network.AzureInterceptor;
import com.spawn.ai.utils.task_utils.AppUtils;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(ApplicationComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public AppUtils provideAppUtils() {
        return new AppUtils();
    }

    @Provides
    @Singleton
    public AzureInterceptor provideAzureInterceptor(AppUtils appUtils) {
        return new AzureInterceptor(appUtils);
    }

    @Named("ForAzureService")
    @Provides
    @Singleton
    public OkHttpClient provideAzureClient(AzureInterceptor azureInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(azureInterceptor)
                .build();
    }

    @Named("ForAzureService")
    @Provides
    @Singleton
    public Retrofit provideAzureRetrofit(@Named("ForAzureService") OkHttpClient okHttpClient, AppUtils appUtils) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public AzureService provideAzureService(@Named("ForAzureService") Retrofit retrofit) {
        return retrofit.create(AzureService.class);
    }
}
