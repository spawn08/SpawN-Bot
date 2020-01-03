package com.spawn.ai;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.spawn.ai.activities.SpawnWebActivity;
import com.spawn.ai.adapters.SpawnChatbotAdapter;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.databinding.ActivitySpawnBotBinding;
import com.spawn.ai.interfaces.IBotObserver;
import com.spawn.ai.interfaces.IBotWikiNLP;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.network.WebServiceUtils;
import com.spawn.ai.utils.views.AlertUpdateDialog;
import com.spawn.ai.utils.task_utils.AppUtils;
import com.spawn.ai.utils.task_utils.DateTimeUtils;
import com.spawn.ai.utils.task_utils.JsonFileReader;
import com.spawn.ai.utils.task_utils.SharedPreferenceUtility;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

public class SpawnBotActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener, IBotObserver, IBotWikiNLP, TextToSpeech.OnInitListener {

    private static final String TAG = SpawnBotActivity.class.getCanonicalName();
    public Context context;
    private ActivitySpawnBotBinding activitySpawnBotBinding;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntentDispatcher;
    private Locale locale;
    private boolean isSpeechEnd = false;
    private CountDownTimer countDownTimer;
    private boolean isSpeechEnabled = false;
    private ArrayList<ChatMessageType> botResponses;
    private SpawnChatbotAdapter chatbotAdapter;
    private TextToSpeech textToSpeech;

    private Animation slideIn;
    private Animation slideOut;
    int textCount;
    private ChatMessageType chatMessage;
    private String lang;

    private static String spokenString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.VERSION_CODE != Integer.parseInt(JsonFileReader.getInstance().getValueFromJson("app_version"))
                && JsonFileReader.getInstance().getValueFromJson("force_update").equalsIgnoreCase("true")) {
            setUpAlertDialog();
        }
        context = this;
        activitySpawnBotBinding = DataBindingUtil.setContentView(this, R.layout.activity_spawn_bot);
        activitySpawnBotBinding.setListener(this);
        locale = new Locale("en");
        requestPermission();

        activitySpawnBotBinding
                .titleText
                .setText(AppUtils.getStringRes(R.string.app_name, this, SharedPreferenceUtility.getInstance(this).getStringPreference("lang")));

        Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("action", "App open"));

        setUpClickListener();

        if (SharedPreferenceUtility.getInstance(this).getPreference("speak")) {
            activitySpawnBotBinding.volumeUp.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.volumeDown.setVisibility(View.GONE);
        } else {
            activitySpawnBotBinding.volumeUp.setVisibility(View.GONE);
            activitySpawnBotBinding.volumeDown.setVisibility(View.VISIBLE);
        }

        activitySpawnBotBinding.langChange.setOnClickListener(this);

        botResponses = new ArrayList<ChatMessageType>();
        chatbotAdapter = new SpawnChatbotAdapter(this, botResponses);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        activitySpawnBotBinding.chatRecycler.setLayoutManager(linearLayoutManager);
        activitySpawnBotBinding.chatRecycler.setAdapter(chatbotAdapter);
        textToSpeech = new TextToSpeech(this, this);

        initSpeech();

        setUpQuestionsView(SharedPreferenceUtility.getInstance(this).getStringPreference("lang"));

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

    private void setUpQuestionsView(String lang) {

        activitySpawnBotBinding.langChange.setText(AppUtils.getStringRes(R.string.language_initials, this, lang));
        if (lang.equalsIgnoreCase("en")) {
            WebServiceUtils.getInstance(this).setToken(getString(R.string.wit_token_en));
        } else {
            WebServiceUtils.getInstance(this).setToken(getString(R.string.wit_token_hi));
        }
        final ArrayList<String> questions = JsonFileReader.getInstance().getQuestions(lang);
        activitySpawnBotBinding.headerText.setText(JsonFileReader.getInstance().getValueFromJson("questions_title_" + lang));

        slideIn = null;
        slideOut = null;
        activitySpawnBotBinding.textviewAnim.setAnimation(null);

        slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in);
        slideOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out);

        slideIn.setRepeatMode(Animation.RESTART);
        slideIn.setRepeatCount(Animation.INFINITE);

        slideOut.setRepeatMode(Animation.RESTART);
        slideOut.setRepeatCount(Animation.INFINITE);

        if (questions.size() > 0)
            activitySpawnBotBinding.textviewAnim.setText(questions.get(0));

        activitySpawnBotBinding.textviewAnim.setAnimation(slideIn);

        slideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (textCount >= questions.size())
                    textCount = 0;

                if (questions.size() > 0)
                    activitySpawnBotBinding.textviewAnim.setText(questions.get(textCount));
                textCount++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activitySpawnBotBinding.textviewAnim.startAnimation(slideOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activitySpawnBotBinding.textviewAnim.startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void setUpClickListener() {
        activitySpawnBotBinding.mic.setOnClickListener(this);
        activitySpawnBotBinding.micImage.setOnClickListener(this);
        activitySpawnBotBinding.recyclerContainer.setOnClickListener(this);
        activitySpawnBotBinding.chatRecycler.setOnClickListener(this);
        activitySpawnBotBinding.arrowBack.setOnClickListener(this);
        activitySpawnBotBinding.volumeUp.setOnClickListener(this);
        activitySpawnBotBinding.volumeDown.setOnClickListener(this);
    }

    private void setUpAlertDialog() {
        AlertUpdateDialog alertUpdateDialog = new AlertUpdateDialog(this);
        alertUpdateDialog.show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(SpawnBotActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET},
                1);
    }

    private void initSpeech() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        String lang = "en";

        if (SharedPreferenceUtility.getInstance(this).getStringPreference("lang").equalsIgnoreCase("hi")) {
            lang = "hi";
        } else {
            lang = "en";
        }

        speechIntentDispatcher = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        speechIntentDispatcher.putExtra("android.speech.extra.DICTATION_MODE", true);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
        }

        speechRecognizer.setRecognitionListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSpeech();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isSpeechEnabled = true;
                } else {
                    isSpeechEnabled = false;
                    Toast.makeText(this, "Permission for speech input is disabled", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "onReadyForSpeech");
        isSpeechEnd = false;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
        if (textToSpeech != null &&
                textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
        activitySpawnBotBinding.containerStop.setOnClickListener(this);
        activitySpawnBotBinding.containerStop.requestFocus();
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
        Crashlytics.log(TAG + " Speech ERROR " + i);
        switch (i) {
            case SpeechRecognizer.ERROR_NETWORK:
                activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                onEndOfSpeech();
                Toast.makeText(this, "No Network", Toast.LENGTH_LONG);
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                onEndOfSpeech();
                Toast.makeText(this, "No permission to perform the action", Toast.LENGTH_LONG);
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                onEndOfSpeech();
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                //activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                new CountDownTimer(1000, 2500) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                    }
                }.start();
               /* if (SpeechRecognizer.isRecognitionAvailable(this) && isSpeechEnabled) {
                    if (speechRecognizer == null)
                        initSpeech();
                    speechRecognizer.startListening(speechIntentDispatcher);
                    activitySpawnBotBinding.micImage.setVisibility(View.GONE);
                    activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
                    activitySpawnBotBinding.mic.playAnimation();

                }*/
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
            spokenString = speechString;
            Log.d(getClass().getCanonicalName(), "Speech :" + speechString);
            onEndOfSpeech();
            chatViews(speechString, 0, null);
            callWitService(speechString);

        }
    }

    private void callWitService(String speechString) {
        WebServiceUtils.getInstance(this).setUpObserver(this);
        WebServiceUtils.getInstance(this).getRetrofitClient();
        WebServiceUtils.getInstance(this).getBotResponse(speechString);
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
            //if (partialString.length() % 2 == 0)
            //   chatViews(partialString, 0, null);
            Log.d(TAG, "partialString :" + partialString);
        }

        countDownTimer = new CountDownTimer(1000, 4000) {
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

    private void chatViews(String chatMessage, int type, ChatCardModel chatCardModel) {

        switch (type) {
            case ChatViewTypes.CHAT_VIEW_USER:
                ChatMessageType chatMessageType = new ChatMessageType();
                chatMessageType.setMessage(chatMessage);
                chatMessageType.setViewType(0);
                chatMessageType.setDate(new DateTimeUtils().getDate());
                chatMessageType.setBotResponse(null);
                chatMessageType.setAction(null);
                if (botResponses.size() == 0)
                    botResponses.add(chatMessageType);
                else {
                    // botResponses.remove(botResponses.size() - 1);
                    botResponses.add(chatMessageType);
                }
                chatbotAdapter.setAdapter(botResponses);

                break;

            case ChatViewTypes.CHAT_VIEW_BOT:
                if (chatCardModel != null) {
                    ChatMessageType chatMessageType1 = new ChatMessageType();
                    chatMessageType1.setMessage(chatCardModel.getMessage());
                    chatMessageType1.setDate(new DateTimeUtils().getDate());
                    chatMessageType1.setViewType(chatCardModel.getType());
                    chatMessageType1.setAction(chatCardModel.getAction());
                    chatMessageType1.setBotResponse(null);
                    if (botResponses.get(botResponses.size() - 1).getViewType() == 2)
                        botResponses.remove(botResponses.size() - 1);
                    botResponses.add(chatMessageType1);
                    chatbotAdapter.setAdapter(botResponses);
                    chatbotAdapter.notifyDataSetChanged();
                    activitySpawnBotBinding.chatRecycler.scrollToPosition(chatbotAdapter.getItemCount() - 1);
                    setChatMessage(chatMessageType1);
                }

                break;

            case ChatViewTypes.CHAT_VIEW_LOADING:
                ChatMessageType chatMessageLoading = new ChatMessageType();
                if (botResponses.size() > 0 && botResponses.get(botResponses.size() - 1).getViewType() == 2)
                    botResponses.remove(botResponses.size() - 1);
                chatMessageLoading.setViewType(2);
                botResponses.add(chatMessageLoading);
                chatbotAdapter.setAdapter(botResponses);
                break;

            case ChatViewTypes.CHAT_VIEW_CARD:
                if (chatCardModel != null) {
                    ChatMessageType chatMessageType1 = new ChatMessageType();
                    chatMessageType1.setMessage(chatCardModel.getMessage());
                    chatMessageType1.setDate(new DateTimeUtils().getDate());
                    chatMessageType1.setButtonText(chatCardModel.getButton_text());
                    chatMessageType1.setViewType(chatCardModel.getType());
                    chatMessageType1.setAction(chatCardModel.getAction());
                    chatMessageType1.setBotResponse(null);
                    botResponses.remove(botResponses.size() - 1);
                    botResponses.add(chatMessageType1);
                    chatbotAdapter.setAdapter(botResponses);
                    chatbotAdapter.notifyDataSetChanged();
                    activitySpawnBotBinding.chatRecycler.scrollToPosition(chatbotAdapter.getItemCount() - 1);
                }
                break;

            case ChatViewTypes.CHAT_VIEW_WIKI:
                if (chatCardModel != null) {
                    ChatMessageType wikiType = new ChatMessageType();
                    wikiType.setSpawnWikiModel(chatCardModel.getSpawnWikiModel());
                    wikiType.setViewType(chatCardModel.getType());
                    botResponses.remove(botResponses.size() - 1);
                    botResponses.add(wikiType);
                    chatbotAdapter.setAdapter(botResponses);
                    chatbotAdapter.notifyDataSetChanged();
                    activitySpawnBotBinding.chatRecycler.scrollToPosition(chatbotAdapter.getItemCount() - 1);
                }

                break;

            case ChatViewTypes.CHAT_VIEW_NEWS:

                if (chatCardModel != null) {
                    ChatMessageType wikiType = new ChatMessageType();
                    wikiType.setSpawnWikiModel(chatCardModel.getSpawnWikiModel());
                    wikiType.setViewType(chatCardModel.getType());
                    botResponses.remove(botResponses.size() - 1);
                    botResponses.add(wikiType);
                    chatbotAdapter.setAdapter(botResponses);
                    chatbotAdapter.notifyDataSetChanged();
                    activitySpawnBotBinding.chatRecycler.scrollToPosition(chatbotAdapter.getItemCount() - 1);
                }

                break;

        }

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.mic) {
            if (speechRecognizer == null)
                initSpeech();
            //initSpeech();
            speechRecognizer.startListening(speechIntentDispatcher);
            activitySpawnBotBinding.micImage.setVisibility(View.GONE);
            activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.mic.playAnimation();
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("action", "Mic Listening"));

        } else if (i == R.id.mic_image) {
            if (activitySpawnBotBinding.recyclerContainer.getVisibility() == View.GONE)
                activitySpawnBotBinding.recyclerContainer.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.textviewAnimation.setVisibility(View.GONE);
            //botResponses.clear();
            chatbotAdapter.setAdapter(botResponses);
            if (SpeechRecognizer.isRecognitionAvailable(this) && isSpeechEnabled) {
                if (speechRecognizer == null)
                    initSpeech();
                //initSpeech();
                speechRecognizer.startListening(speechIntentDispatcher);
                activitySpawnBotBinding.micImage.setVisibility(View.GONE);
                activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
                activitySpawnBotBinding.mic.playAnimation();
            } else {
                if (speechRecognizer != null) {
                    speechRecognizer.cancel();
                    speechRecognizer.destroy();
                }
                Toast.makeText(this, "Permission for speech input is disabled", Toast.LENGTH_LONG).show();
                requestPermission();
            }
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("action", "Mic Listening"));

        } else if (i == R.id.recycler_container) {

            if (textToSpeech != null &&
                    textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
        } else if (i == R.id.volume_down) {
            setUpVolumeButton(false);
        } else if (i == R.id.volume_up) {
            setUpVolumeButton(true);

        } else if (i == R.id.chat_recycler) {

            if (textToSpeech != null &&
                    textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
        } /*else if (i == R.id.container_stop) {
            activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
            if (textToSpeech != null &&
                    textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
        }*/ else if (i == R.id.arrow_back) {
            finish();
        } else if (i == R.id.lang_change) {
            if (SharedPreferenceUtility.getInstance(this).getStringPreference("lang").equalsIgnoreCase("en")) {
                SharedPreferenceUtility.getInstance(this).storeStringPreference("lang", "hi");
                initSpeech();
                textToSpeech.stop();
                textToSpeech = null;
                textToSpeech = new TextToSpeech(this, this);
                activitySpawnBotBinding.langChange.setText(AppUtils.getStringRes(R.string.language_initials, this, "hi"));
                activitySpawnBotBinding
                        .titleText
                        .setText(AppUtils.getStringRes(R.string.app_name, this, "hi"));
                WebServiceUtils.getInstance(this).setToken(getString(R.string.wit_token_hi));
                botResponses.clear();
                chatbotAdapter.setAdapter(botResponses);
                chatbotAdapter.notifyDataSetChanged();

                activitySpawnBotBinding.recyclerContainer.setVisibility(View.GONE);
                activitySpawnBotBinding.textviewAnimation.setVisibility(View.VISIBLE);

                updateLanguageConfig("hi");
                WebServiceUtils.getInstance(this).setLanguage("hi");
                setUpQuestionsView("hi");
            } else {
                SharedPreferenceUtility.getInstance(this).storeStringPreference("lang", "en");
                initSpeech();
                textToSpeech.stop();
                textToSpeech = null;
                textToSpeech = new TextToSpeech(this, this);
                WebServiceUtils.getInstance(this).setToken(getString(R.string.wit_token_en));
                activitySpawnBotBinding.langChange
                        .setText(AppUtils.getStringRes(R.string.language_initials, this, "en"));
                activitySpawnBotBinding
                        .titleText
                        .setText(AppUtils.getStringRes(R.string.app_name, this, "en"));

                botResponses.clear();
                chatbotAdapter.setAdapter(botResponses);
                chatbotAdapter.notifyDataSetChanged();

                activitySpawnBotBinding.recyclerContainer.setVisibility(View.GONE);
                activitySpawnBotBinding.textviewAnimation.setVisibility(View.VISIBLE);

                updateLanguageConfig("en");
                WebServiceUtils.getInstance(this).setLanguage("en");
                setUpQuestionsView("en");
            }
        }

    }

    private void updateLanguageConfig(String lang) {
        Locale locale = new Locale(lang);
        Configuration overrideConfiguration = SpawnAiApplication.getContext().getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            overrideConfiguration.setLocale(locale);
        } else {
            overrideConfiguration.locale = locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            createConfigurationContext(overrideConfiguration);
        } else {
            getResources().updateConfiguration(overrideConfiguration, SpawnAiApplication.getContext().getResources().getDisplayMetrics());
        }
    }

    public void setUpVolumeButton(boolean setup) {
        if (setup) {
            if (activitySpawnBotBinding.volumeDown.getVisibility() == View.GONE) {
                activitySpawnBotBinding.volumeDown.setVisibility(View.VISIBLE);
                activitySpawnBotBinding.volumeUp.setVisibility(View.GONE);
                SharedPreferenceUtility.getInstance(this).storePreference("speak", false);
                if (chatMessage != null)
                    chatMessage.setSpeakFinish(true);
                notifyBotError();
            } else {
                activitySpawnBotBinding.volumeUp.setVisibility(View.VISIBLE);
                activitySpawnBotBinding.volumeDown.setVisibility(View.GONE);
                SharedPreferenceUtility.getInstance(this).storePreference("speak", true);
                /*if (chatMessage != null)
                    speakBot(chatMessage.getShortMessage());*/

            }
        } else {
            if (activitySpawnBotBinding.volumeDown.getVisibility() == View.GONE) {
                activitySpawnBotBinding.volumeDown.setVisibility(View.VISIBLE);
                activitySpawnBotBinding.volumeUp.setVisibility(View.GONE);
                SharedPreferenceUtility.getInstance(this).storePreference("speak", false);
                if (chatMessage != null)
                    chatMessage.setSpeakFinish(true);
                notifyBotError();
            } else {
                activitySpawnBotBinding.volumeUp.setVisibility(View.VISIBLE);
                activitySpawnBotBinding.volumeDown.setVisibility(View.GONE);
                SharedPreferenceUtility.getInstance(this).storePreference("speak", true);
                /*if (chatMessage != null)
                    speakBot(chatMessage.getShortMessage());*/

            }
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
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
                textToSpeech = null;
            }
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("action", "App close"));
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

    }

    @Override
    public void notifyBotResponse(ChatCardModel chatCardModel) {
        chatViews(null, chatCardModel.getType(), chatCardModel);
    }

    @Override
    public void notifyBotError() {
        if (textToSpeech != null &&
                textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    @Override
    public void loading() {
        chatViews(null, 2, null);
    }

    @Override
    public void speakBot(String message) {
        if (Build.VERSION.SDK_INT < 21) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("action", "App Speaking"));
        } else {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "10000");
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("action", "App Speaking"));
        }
    }

    @Override
    public void setAction(String action, SpawnWikiModel spawnWikiModel) {
        Handler handler = new Handler();
        if (action.equals("web_action")) {
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("action", "Web Open"));
            Intent intent = new Intent(this, SpawnWebActivity.class);
            intent.putExtra("url", spawnWikiModel.getContent_urls().getMobile().getPage());
            startActivity(intent);
        } else if (action.equals("finish")) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);

        } else if (action.equals("speak")) {
            Answers.getInstance()
                    .logCustom(new CustomEvent(this.getClass().getSimpleName())
                            .putCustomAttribute("action", "Context conversation"));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startListen();
                }
            }, 2500);

        } else if (action.equalsIgnoreCase("google_search")) {
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("action", "Google Search"));
            Intent intent = new Intent(this, SpawnWebActivity.class);
            intent.putExtra("url", getResources().getString(R.string.google_search) + spokenString);
            startActivity(intent);
        }

    }

    @Override
    public void setChatMessage(ChatMessageType chatMessage) {
        this.chatMessage = chatMessage;
    }

    private void startListen() {
        if (SpeechRecognizer.isRecognitionAvailable(this) && isSpeechEnabled) {
            //botResponses.clear();
            chatbotAdapter.setAdapter(botResponses);
            if (speechRecognizer == null)
                initSpeech();
            //initSpeech();
            speechRecognizer.startListening(speechIntentDispatcher);
            activitySpawnBotBinding.micImage.setVisibility(View.GONE);
            activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.mic.playAnimation();
        }
    }


    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = -1;
            String lang = SharedPreferenceUtility.getInstance(this).getStringPreference("lang");
            textToSpeech.setLanguage(new Locale(lang));
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("TTSLanguage", lang));
            textToSpeech.setPitch(0.80f);
            textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(this.getClass().getName(), "This Language is not supported");
            } else {
                Log.d(this.getClass().getName(), "Initilization Success!");
            }
        } else {
            Log.e(this.getClass().getName(), "Initilization Failed!");
            Answers.getInstance().logCustom(new CustomEvent(this.getClass().getSimpleName()).putCustomAttribute("ttsInitialization", "Failure"));
        }
    }

    @Override
    public void showUI(ChatCardModel chatCardModel) {
        chatViews(null, chatCardModel.getType(), chatCardModel);
    }

    UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {

        }

        @Override
        public void onDone(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                }
            });

        }

        @Override
        public void onError(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                }
            });
        }
    };
}
