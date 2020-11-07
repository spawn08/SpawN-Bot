package com.spawn.ai.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.spawn.ai.R;
import com.spawn.ai.SpawnBotActivity;
import com.spawn.ai.utils.task_utils.AppUtils;
import com.spawn.ai.utils.task_utils.JsonFileReader;
import com.spawn.ai.utils.task_utils.SharedPreferenceUtility;
import com.spawn.ai.viewmodels.WebSearchViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class SpawnSplashScreen extends AppCompatActivity {

    public LottieAnimationView spawnLogo;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spawn_splash_screen);
        context = this;
        WebSearchViewModel webSearchViewModel = new ViewModelProvider(this).get(WebSearchViewModel.class);

        spawnLogo = findViewById(R.id.spawn_logo);
        spawnLogo.setRepeatCount(LottieDrawable.INFINITE);
        spawnLogo.setRepeatMode(LottieDrawable.RESTART);
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
        webSearchViewModel
                .getFile(AppUtils.getInstance().getDataFile())
                .observe(this, jsonElement -> {
                            if (jsonElement != null) {
                                JsonFileReader.getInstance().fileName(AppUtils.getInstance().getDataFile());
                                JsonFileReader.getInstance().readFile(this, jsonElement);
                                JsonFileReader.getInstance().setQuestions(SharedPreferenceUtility.getInstance(this).getStringPreference("lang"));
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(context, SpawnBotActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1000);

                            } else {
                                JsonFileReader.getInstance().readFile(this, null);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(context, SpawnBotActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 500);

                            }
                        }
                );
    }
}
