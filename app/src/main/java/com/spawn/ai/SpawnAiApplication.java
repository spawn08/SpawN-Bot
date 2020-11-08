package com.spawn.ai;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.spawn.ai.utils.task_utils.AppUtils;

import androidx.annotation.NonNull;

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
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        AppUtils.getInstance().setToken(task.getResult().getToken());
                    }
                });
    }

    public static Context getContext() {
        return context;
    }
}
