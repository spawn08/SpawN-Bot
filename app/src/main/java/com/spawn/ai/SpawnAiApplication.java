package com.spawn.ai;

import android.app.Application;

import com.google.firebase.iid.FirebaseInstanceId;
import com.spawn.ai.utils.task_utils.AppUtils;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class SpawnAiApplication extends Application {

    @Inject
    AppUtils appUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task ->
                        appUtils.setToken(task.getResult().getToken()));
    }
}
