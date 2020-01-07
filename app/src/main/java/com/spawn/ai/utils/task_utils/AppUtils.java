package com.spawn.ai.utils.task_utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.spawn.ai.R;
import com.spawn.ai.SpawnBotActivity;

import org.json.JSONArray;

import java.util.Locale;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppUtils {

    private static AppUtils appUtils;
    private JSONArray jsonArray;

    private AppUtils() {

    }

    public static AppUtils getInstance() {
        if (appUtils == null) {
            appUtils = new AppUtils();
        }

        return appUtils;
    }

    public static String getStringRes(int resourceId, Context context, String lang) {
        String result = "";

        Locale requestedLocale = new Locale(lang);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Configuration config = new Configuration(context.getResources().getConfiguration());
            config.setLocale(requestedLocale);
            result = context.createConfigurationContext(config).getText(resourceId).toString();
        }

        return result;
    }

    public void setNewsJSON(JSONArray jsonArray) {
        this.jsonArray = jsonArray;

    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void sendDefaultNotification(Map<String, String> data, Context context) {
        Intent intent = new Intent(context, SpawnBotActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context,
                        101,
                        intent,
                        0);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel = new NotificationChannel("222", "vehicle_damage", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        "222")
                        .setContentTitle(data.get("title"))
                        .setAutoCancel(true)
                        .setLargeIcon(((BitmapDrawable) context.getDrawable(R.mipmap.ic_launcher)).getBitmap())
                        .setSound(defaultSound)
                        .setContentText(data.get("body"))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public String getInfoFromExtract(String extract, String type) {
        String text = "";
        try {
            String[] splitExtract = extract.split("\\.");
            if (type.equals("speak")) {
                text = splitExtract[0];
            } else {
                if (splitExtract.length > 1) {
                    text = splitExtract[0] + ". " + splitExtract[1] + "..";
                } else {
                    text = splitExtract[0];
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return text + ".";
    }
}
