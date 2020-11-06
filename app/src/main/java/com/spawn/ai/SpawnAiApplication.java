package com.spawn.ai;

import android.app.Application;
import android.content.Context;

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
    }

    public static Context getContext() {
        return context;
    }
}
