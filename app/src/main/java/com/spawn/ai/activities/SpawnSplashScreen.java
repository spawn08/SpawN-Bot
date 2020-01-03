package com.spawn.ai.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.crashlytics.android.Crashlytics;
import com.spawn.ai.R;
import com.spawn.ai.SpawnBotActivity;
import com.spawn.ai.network.WebServiceUtils;
import com.spawn.ai.utils.task_utils.JsonFileReader;
import com.spawn.ai.utils.task_utils.SharedPreferenceUtility;

import constants.AppConstants;

public class SpawnSplashScreen extends AppCompatActivity {

    public LottieAnimationView spawnLogo;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spawn_splash_screen);
        context = this;
        spawnLogo = (LottieAnimationView) findViewById(R.id.spawn_logo);
        spawnLogo.setRepeatMode(LottieDrawable.INFINITE);
        spawnLogo.playAnimation();
        spawnLogo.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                spawnLogo.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                   /* WebServiceUtils.getInstance(SpawnSplashScreen.this)
                            .getFile(AppConstants.DATA_FILE_SERVER, SpawnSplashScreen.this);*/

                    JsonFileReader.getInstance().fileName(AppConstants.DATA_FILE);
                    JsonFileReader.getInstance().readFile(SpawnSplashScreen.this, null);
                    JsonFileReader.getInstance().setQuestions(SharedPreferenceUtility.getInstance(SpawnSplashScreen.this).getStringPreference("lang"));

                    WebServiceUtils.getInstance(SpawnSplashScreen.this)
                            .setLanguage(SharedPreferenceUtility
                                    .getInstance(SpawnSplashScreen.this)
                                    .getStringPreference(AppConstants.LANG));

                    Intent intent = new Intent(SpawnSplashScreen.this, SpawnBotActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }
        }, 2000);
    }
}
