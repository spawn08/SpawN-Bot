package com.spawn.ai.services;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.spawn.ai.utils.AppUtils;

import java.util.Map;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static androidx.constraintlayout.widget.Constraints.TAG;

@AndroidEntryPoint
public class NotificationService extends FirebaseMessagingService {

    @Inject
    AppUtils appUtils;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                String token = task.getResult().getToken();
                Log.e("My Token", token);
                appUtils.setToken(token);
            }
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> dataMessage = remoteMessage.getData();
        String type = dataMessage.get("type");
        if (type != null &&
                type.equalsIgnoreCase("default")) {
            appUtils.sendDefaultNotification(dataMessage, getApplicationContext());
        }

    }
}
