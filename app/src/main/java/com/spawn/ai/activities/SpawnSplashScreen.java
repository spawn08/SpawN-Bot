package com.spawn.ai.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.crashlytics.android.Crashlytics;
import com.spawn.ai.R;
import com.spawn.ai.SpawnBotActivity;
import com.spawn.ai.network.WebServiceUtils;
import com.spawn.ai.utils.JsonFileReader;

import constants.AppConstants;
import io.fabric.sdk.android.Fabric;

public class SpawnSplashScreen extends AppCompatActivity {

    public LottieAnimationView spawnLogo;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spawn_splash_screen);
        context = this;
        //JsonFileReader.getInstance().readFile(this);
        try {
            WebServiceUtils.getInstance(this).setToken(getResources().getString(R.string.wit_token));
            WebServiceUtils.getInstance(this).getFile(AppConstants.DATA_FILE_SERVER, context);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        spawnLogo = (LottieAnimationView) findViewById(R.id.spawn_logo);
        spawnLogo.setRepeatMode(-1);
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
                Intent intent = new Intent(context, SpawnBotActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
