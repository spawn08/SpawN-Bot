package com.spawn.ai;

import android.app.Application;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.spawn.ai.utils.task_utils.AppUtils;

import androidx.annotation.NonNull;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class SpawnAiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        AppUtils.getInstance().setToken(task.getResult().getToken());
                    }
                });
    }
}
