package com.spawn.ai.utils.task_utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.spawn.ai.R;
import com.spawn.ai.activities.SpawnBotActivity;
import com.spawn.ai.custom.AlertUpdateDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.spawn.ai.constants.AppConstants.CONNECTIVITY_CHANGE_ACTION;

@Singleton
public class AppUtils {

    private String token;

    @Inject
    public AppUtils() {

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

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
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
        NotificationChannel channel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel = new NotificationChannel("222", "professor_spawn", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        "222")
                        .setContentTitle(data.get("title"))
                        .setAutoCancel(true)
                        .setLargeIcon(getNotificationDrawable(context))
                        .setSound(defaultSound)
                        .setContentText(data.get("body"))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private Bitmap getNotificationDrawable(Context context) {
        return ((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context, R.mipmap.ic_launcher))).getBitmap();
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
            FirebaseCrashlytics.getInstance().log(e.toString());
        }
        return text + ".";
    }

    public String checkForRegex(String speechString, String language) {
        try {
            Pattern pattern;
            JSONObject jsonObject = new JSONObject(JsonFileReader.getInstance().getFileContents());
            JSONObject regex = jsonObject.getJSONObject("regex");
            JSONArray jsonArray = regex.getJSONArray(language);
            for (int i = 0; i < jsonArray.length(); i++) {
                pattern = Pattern.compile(jsonArray.getString(i));
                Matcher matcher = pattern.matcher(speechString);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Show alertdialog for app version update
     *
     * @param activity Calling activity context
     */
    public void showVersionUpdateDialog(Activity activity) {
        AlertUpdateDialog alertUpdateDialog = new AlertUpdateDialog(activity);
        alertUpdateDialog.show();
    }

    /**
     * Create Intent for checking the connectivity
     *
     * @param noConnection boolean flag for connectivity
     * @return intent Intent object
     */
    public Intent getConnectivityIntent(boolean noConnection) {
        Intent intent = new Intent();
        intent.setAction(CONNECTIVITY_CHANGE_ACTION);
        intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, noConnection);
        return intent;
    }

    /**
     * Create input features for given sentence query.
     * The below method will create bag of words features for input sentence.
     *
     * @param sentence   user query for which input features is to be created
     * @param input      float[][] for input features
     * @param wordsArray Array of stemmed words
     * @param stemmer    {@link LancasterStemmer} object for stemming the words
     * @param isEn       if true, then language is en or hi
     * @return input 2-D input feature array
     */
    public float[][] getInputFeatures(String sentence,
                                      float[][] input,
                                      ArrayList<String> wordsArray,
                                      LancasterStemmer stemmer,
                                      boolean isEn) {
        try {
            String[] sent = sentence.split(" ");
            for (String s : sent) {
                String stemmedWord = stemmer.stem(s);
                for (int i = 0; i < wordsArray.size(); i++) {
                    String wordIndex = wordsArray.get(i);
                    if (stemmedWord.equalsIgnoreCase(wordIndex) && isEn)
                        input[0][i] = 1.0f;
                }
            }

            return input;
        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
    }

    /**
     * Get the index of largest float value in an array of float probabilities
     *
     * @param array input float array
     * @return largest index of the largest element
     */
    public int getIndexOfLargest(float[] array) {
        if (array == null || array.length == 0) return -1;

        int largest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[largest]) largest = i;
        }
        return largest;
    }

    /**
     * Load json file from assets folder in android
     *
     * @param context  context of calling activity
     * @param fileName name of the file to load the file from assets
     * @return string data
     */
    public String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
