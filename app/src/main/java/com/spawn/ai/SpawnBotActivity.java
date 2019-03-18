package com.spawn.ai;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spawn.ai.databinding.ActivitySpawnBotBinding;

import java.util.ArrayList;
import java.util.Locale;

public class SpawnBotActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener {

    private static final String TAG = SpawnBotActivity.class.getCanonicalName();
    public Context context;
    private ActivitySpawnBotBinding activitySpawnBotBinding;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntentDispatcher;
    private Locale locale;
    private boolean isSpeechEnd = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activitySpawnBotBinding = DataBindingUtil.setContentView(this, R.layout.activity_spawn_bot);
        activitySpawnBotBinding.setListener(this);
        locale = new Locale("en");

        activitySpawnBotBinding.mic.setOnClickListener(this);
        activitySpawnBotBinding.micImage.setOnClickListener(this);
        initSpeech();

        activitySpawnBotBinding.mic.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isSpeechEnd) {
                    activitySpawnBotBinding.mic.invalidate();
                    activitySpawnBotBinding.mic.cancelAnimation();
                    activitySpawnBotBinding.micImage.setVisibility(View.VISIBLE);
                    activitySpawnBotBinding.mic.setVisibility(View.GONE);
                } else {
                    activitySpawnBotBinding.mic.playAnimation();
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void initSpeech() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechIntentDispatcher = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("en"));
        speechIntentDispatcher.putExtra("android.speech.extra.DICTATION_MODE", true);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
        }

        speechRecognizer.setRecognitionListener(this);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "onReadyForSpeech");
        isSpeechEnd = false;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
        isSpeechEnd = true;
        activitySpawnBotBinding.mic.invalidate();
        activitySpawnBotBinding.mic.cancelAnimation();
        activitySpawnBotBinding.micImage.setVisibility(View.VISIBLE);
        activitySpawnBotBinding.mic.setVisibility(View.GONE);

    }

    @Override
    public void onError(int i) {
        Log.d(TAG, "ERROR " + i);
        switch (i) {
            case SpeechRecognizer.ERROR_NETWORK:
                onEndOfSpeech();
                Toast.makeText(this, "No Network", Toast.LENGTH_LONG);
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                onEndOfSpeech();
                Toast.makeText(this, "No permission to perform the action", Toast.LENGTH_LONG);
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                onEndOfSpeech();
                break;
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        if (bundle != null && bundle.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            activitySpawnBotBinding.mic.cancelAnimation();
            activitySpawnBotBinding.mic.invalidate();
            ArrayList<String> returnSpeech = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String speechString = returnSpeech.get(0);
            onEndOfSpeech();
            Log.d(getClass().getCanonicalName(), "Speech :" + speechString);

        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        if (countDownTimer != null)
            countDownTimer.cancel();
        Log.d(TAG, "onPartialResults");
        activitySpawnBotBinding.mic.playAnimation();

        if (bundle != null
                && bundle.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)
                && bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0) {
            ArrayList<String> partialResults = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String partialString = partialResults.get(0);

            Log.d(TAG, "partialString :" + partialString);
        }

        countDownTimer = new CountDownTimer(1000, 3000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (speechRecognizer != null)
                    speechRecognizer.stopListening();

                onEndOfSpeech();
                Log.d(TAG, "onEndOfSpeech from Countdowntimer");

            }
        }.start();
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mic:
                initSpeech();
                speechRecognizer.startListening(speechIntentDispatcher);
                //activitySpawnBotBinding.mic.setBackground(getResources().getDrawable(android.R.color.transparent));
                activitySpawnBotBinding.micImage.setVisibility(View.GONE);
                activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
                activitySpawnBotBinding.mic.playAnimation();

                break;

            case R.id.mic_image:
                if (SpeechRecognizer.isRecognitionAvailable(this)) {
                    initSpeech();
                    speechRecognizer.startListening(speechIntentDispatcher);
                    activitySpawnBotBinding.micImage.setVisibility(View.GONE);
                    activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
                    activitySpawnBotBinding.mic.playAnimation();
                } else {
                    if (speechRecognizer != null) {
                        speechRecognizer.cancel();
                        speechRecognizer.destroy();
                    }

                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (speechRecognizer != null) {
                speechRecognizer.cancel();
                speechRecognizer.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
