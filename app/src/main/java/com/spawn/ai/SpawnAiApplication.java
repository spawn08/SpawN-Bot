package com.spawn.ai;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class SpawnAiApplication extends Application {

    private static Context context;
    private static SpawnAiApplication spawnAiApplication;

    public static SpawnAiApplication getInstance() {
        if (spawnAiApplication == null) {
            spawnAiApplication = new SpawnAiApplication();
        }

        return spawnAiApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Fabric.with(this, new Crashlytics());

    }

    public static Context getContext() {
        return context;
    }
}
