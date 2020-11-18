package com.spawn.ai.activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
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

import com.google.firebase.BuildConfig;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.spawn.ai.R;
import com.spawn.ai.adapters.SpawnChatbotAdapter;
import com.spawn.ai.constants.AppConstants;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.databinding.ActivitySpawnBotBinding;
import com.spawn.ai.di.modules.viewmodels.ViewModelFactory;
import com.spawn.ai.interfaces.AzureService;
import com.spawn.ai.interfaces.IBotObserver;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.model.websearch.ValueResults;
import com.spawn.ai.utils.task_utils.AppUtils;
import com.spawn.ai.utils.task_utils.BotUtils;
import com.spawn.ai.utils.task_utils.DateTimeUtils;
import com.spawn.ai.utils.task_utils.JsonFileReader;
import com.spawn.ai.utils.task_utils.SharedPreferenceUtility;
import com.spawn.ai.viewmodels.ClassifyIntentViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.hilt.android.AndroidEntryPoint;

import static com.spawn.ai.constants.AppConstants.ACTION;
import static com.spawn.ai.constants.AppConstants.FORCE_UPDATE;
import static com.spawn.ai.constants.AppConstants.LANG;
import static com.spawn.ai.constants.AppConstants.LANG_EN;
import static com.spawn.ai.constants.AppConstants.LANG_HI;
import static com.spawn.ai.constants.AppConstants.SPEAK;

@AndroidEntryPoint
public class SpawnBotActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener, IBotObserver, TextToSpeech.OnInitListener {

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
    private int textCount;
    private ChatMessageType chatMessage;
    private ClassifyIntentViewModel classifyViewModel;

    private static String spokenString = "";
    private String language;

    @Inject
    AppUtils appUtils;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.VERSION_CODE != Integer.parseInt(JsonFileReader.getInstance().getValueFromJson("app_version"))
                && JsonFileReader.getInstance().getValueFromJson(FORCE_UPDATE).equalsIgnoreCase("true")) {
            appUtils.showVersionUpdateDialog(this);
        }
        context = this;
        activitySpawnBotBinding = DataBindingUtil.setContentView(this, R.layout.activity_spawn_bot);
        activitySpawnBotBinding.setListener(this);
        locale = new Locale(LANG_EN);
        requestPermission();
        registerConnectivityNetworkMonitorForAPI21AndUp();
        activitySpawnBotBinding
                .titleText
                .setText(AppUtils.getStringRes(R.string.app_name, this, SharedPreferenceUtility.getInstance(this).getStringPreference(LANG)));

        FirebaseCrashlytics.getInstance().setCustomKey(ACTION, "App open");

        setUpClickListener();

        if (SharedPreferenceUtility.getInstance(this).getPreference(SPEAK)) {
            activitySpawnBotBinding.volumeUp.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.volumeDown.setVisibility(View.GONE);
        } else {
            activitySpawnBotBinding.volumeUp.setVisibility(View.GONE);
            activitySpawnBotBinding.volumeDown.setVisibility(View.VISIBLE);
        }

        activitySpawnBotBinding.langChange.setOnClickListener(this);

        botResponses = new ArrayList<>();
        chatbotAdapter = new SpawnChatbotAdapter(this, botResponses, appUtils);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        activitySpawnBotBinding.chatRecycler.setLayoutManager(linearLayoutManager);
        activitySpawnBotBinding.chatRecycler.setAdapter(chatbotAdapter);
        textToSpeech = new TextToSpeech(this, this);

        classifyViewModel = new ViewModelProvider(this, viewModelFactory).get(ClassifyIntentViewModel.class);

        initSpeech();

        setUpQuestionsView(SharedPreferenceUtility.getInstance(this).getStringPreference(LANG));

        activitySpawnBotBinding.mic.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isSpeechEnd) {
                    showMic();
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

    /**
     * The method shows mic and cancel the lottie animation
     * while in listening mode.
     */
    private void showMic() {
        activitySpawnBotBinding.mic.invalidate();
        activitySpawnBotBinding.mic.cancelAnimation();
        activitySpawnBotBinding.micImage.setVisibility(View.VISIBLE);
        activitySpawnBotBinding.mic.setVisibility(View.GONE);
    }

    private void setUpQuestionsView(String lang) {
        this.language = lang;
        activitySpawnBotBinding.langChange.setText(AppUtils.getStringRes(R.string.language_initials, this, lang));
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(SpawnBotActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET},
                1);
    }

    private void initSpeech() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        if (SharedPreferenceUtility.getInstance(this).getStringPreference(LANG).equalsIgnoreCase(LANG_HI)) {
            this.language = LANG_HI;
        } else {
            this.language = LANG_EN;
        }

        speechIntentDispatcher = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        speechIntentDispatcher.putExtra("android.speech.extra.DICTATION_MODE", true);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
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
        BotUtils.getInstance().buildInterpreter(this,
                SharedPreferenceUtility.getInstance(this).getStringPreference(LANG));

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("com.spawn.ai.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isSpeechEnabled = true;
            } else {
                isSpeechEnabled = false;
                Toast.makeText(this, "Permission for speech input is disabled", Toast.LENGTH_LONG).show();
            }
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
        showMic();
    }

    @Override
    public void onError(int i) {
        Log.d(TAG, "ERROR " + i);
        FirebaseCrashlytics.getInstance().log(TAG + " Speech ERROR " + i);
        switch (i) {
            case SpeechRecognizer.ERROR_NETWORK:
                activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                onEndOfSpeech();
                Toast.makeText(this, "No Network", Toast.LENGTH_LONG).show();
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                onEndOfSpeech();
                Toast.makeText(this, "No permission to perform the action", Toast.LENGTH_LONG).show();
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                onEndOfSpeech();
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                new CountDownTimer(1000, 2500) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        activitySpawnBotBinding.containerStop.setVisibility(View.GONE);
                    }
                }.start();
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
            String speechString = Objects.requireNonNull(returnSpeech).get(0);
            spokenString = speechString;
            Log.d(getClass().getCanonicalName(), "Speech :" + speechString);
            onEndOfSpeech();
            chatViews(speechString, 0, null);
            classifyIntent(speechString);

        }
    }

    private void classifyIntent(String speechString) {
        loading();
        String entity = appUtils.checkForRegex(speechString, language);
        if (entity != null) {
            classifyViewModel.getWikiResponse(entity, language)
                    .observe(this, (
                            chatCardModel -> {
                                if (chatCardModel != null)
                                    chatViews(null,
                                            chatCardModel.getType(),
                                            chatCardModel);
                                else
                                    onFailure();
                            }
                    ));
        } else {
            classifyViewModel.classify(speechString, language).observe(this, (this::onChanged));
        }
    }

    private void onFailure() {
        try {
            classifyViewModel
                    .getWebSearch(spokenString,
                            SharedPreferenceUtility.getInstance(this).getStringPreference(LANG),
                            AppConstants.RESULT_TYPE_NEWS)
                    .observe(this,
                            (chatCardModel ->
                                    chatViews(null,
                                            chatCardModel.getType(),
                                            chatCardModel)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSuccess(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("message_" + language);
            ChatCardModel chatBotResponseModel = new ChatCardModel();
            int index = new Random().nextInt(jsonArray.length());
            chatBotResponseModel.setMessage(jsonArray.get(index).toString());
            chatBotResponseModel.setType(jsonObject.getInt("type"));
            chatBotResponseModel.setLang(language);
            chatBotResponseModel.setAction(jsonObject.getString(ACTION));
            chatViews("", ChatViewTypes.CHAT_VIEW_BOT, chatBotResponseModel);
        } catch (Exception e) {
            e.printStackTrace();
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
                && Objects.requireNonNull(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)).size() > 0) {
            ArrayList<String> partialResults = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if (Objects.requireNonNull(partialResults).get(0) != null) {
                String partialString = partialResults.get(0);
                Log.d(TAG, "partialString :" + partialString);
            } else {
                Log.e(TAG, "Error Partial Results");
            }
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
                ChatMessageType chatMessageType = ChatMessageType.builder()
                        .message(chatMessage)
                        .viewType(0)
                        .date(DateTimeUtils.getDate())
                        .botResponse(null)
                        .action(null)
                        .build();
                botResponses.add(chatMessageType);
                chatbotAdapter.setAdapter(botResponses);
                break;

            case ChatViewTypes.CHAT_VIEW_BOT:
                ChatMessageType chatViewBot = ChatMessageType.builder()
                        .message(chatCardModel.getMessage())
                        .date(DateTimeUtils.getDate())
                        .viewType(chatCardModel.getType())
                        .action(chatCardModel.getAction())
                        .botResponse(null)
                        .build();
                refreshChat(chatViewBot);
                setChatMessage(chatViewBot);

                break;

            case ChatViewTypes.CHAT_VIEW_LOADING:
                ChatMessageType chatMessageLoading = ChatMessageType.builder()
                        .viewType(2)
                        .build();
                refreshChat(chatMessageLoading);
                break;

            case ChatViewTypes.CHAT_VIEW_CARD:
                ChatMessageType chatViewCard = ChatMessageType.builder()
                        .message(chatCardModel.getMessage())
                        .date(DateTimeUtils.getDate())
                        .buttonText(chatCardModel.getButton_text())
                        .viewType(chatCardModel.getType())
                        .action(chatCardModel.getAction())
                        .botResponse(null)
                        .build();
                refreshChat(chatViewCard);
                break;

            case ChatViewTypes.CHAT_VIEW_WIKI:
                if (chatCardModel != null) {
                    ChatMessageType wikiType = ChatMessageType.builder()
                            .spawnWikiModel(chatCardModel.getSpawnWikiModel())
                            .viewType(chatCardModel.getType())
                            .build();
                    refreshChat(wikiType);
                } else {
                    if (botResponses.get(botResponses.size() - 1).getViewType() == 2)
                        botResponses.remove(botResponses.size() - 1);
                }

                break;

            case ChatViewTypes.CHAT_VIEW_WEB:
                if (chatCardModel != null) {
                    ChatMessageType webSearch = ChatMessageType.builder()
                            .chatCardModel(chatCardModel)
                            .viewType(chatCardModel.getType())
                            .message(AppUtils.getStringRes(R.string.result_text, context,
                                    SharedPreferenceUtility.getInstance(this).getStringPreference(LANG)))
                            .build();

                    refreshChat(webSearch);
                } else {
                    if (botResponses.get(botResponses.size() - 1).getViewType() == 2)
                        botResponses.remove(botResponses.size() - 1);
                }

                break;

            case ChatViewTypes.CHAT_VIEW_NEWS:
                ChatMessageType newsType = ChatMessageType.builder()
                        .chatCardModel(chatCardModel)
                        .viewType(chatCardModel.getType())
                        .build();
                refreshChat(newsType);
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
            FirebaseCrashlytics.getInstance().setCustomKey(ACTION, "Mic Listening");

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
            FirebaseCrashlytics.getInstance().setCustomKey(ACTION, "Mic Listening");

        } else if (i == R.id.recycler_container) {

            if (textToSpeech != null &&
                    textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
        } else if (i == R.id.volume_down) {
            setUpVolumeButton();
        } else if (i == R.id.volume_up) {
            setUpVolumeButton();

        } else if (i == R.id.chat_recycler) {

            if (textToSpeech != null &&
                    textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
        } else if (i == R.id.arrow_back) {
            finish();
        } else if (i == R.id.lang_change) {
            if (SharedPreferenceUtility.getInstance(this)
                    .getStringPreference(LANG).equalsIgnoreCase(LANG_EN)) {
                SharedPreferenceUtility.getInstance(this).storeStringPreference(LANG, LANG_HI);
                initSpeech();
                textToSpeech.stop();
                textToSpeech = null;
                textToSpeech = new TextToSpeech(this, this);
                activitySpawnBotBinding.langChange.setText(AppUtils.getStringRes(R.string.language_initials, this, LANG_HI));
                activitySpawnBotBinding
                        .titleText
                        .setText(AppUtils.getStringRes(R.string.app_name, this, LANG_HI));
                // WebServiceUtils.getInstance().setToken(getString(R.string.wit_token_hi));
                botResponses.clear();
                chatbotAdapter.setAdapter(botResponses);
                chatbotAdapter.notifyDataSetChanged();

                activitySpawnBotBinding.recyclerContainer.setVisibility(View.GONE);
                activitySpawnBotBinding.textviewAnimation.setVisibility(View.VISIBLE);

                updateLanguageConfig(LANG_HI);
                // WebServiceUtils.getInstance().setLanguage("hi");
                setUpQuestionsView(LANG_HI);
                BotUtils.getInstance().buildInterpreter(this, LANG_HI);
            } else {
                SharedPreferenceUtility.getInstance(this).storeStringPreference(LANG, LANG_EN);
                initSpeech();
                textToSpeech.stop();
                textToSpeech = null;
                textToSpeech = new TextToSpeech(this, this);
                //WebServiceUtils.getInstance().setToken(getString(R.string.wit_token_en));
                activitySpawnBotBinding.langChange
                        .setText(AppUtils.getStringRes(R.string.language_initials, this, LANG_EN));
                activitySpawnBotBinding
                        .titleText
                        .setText(AppUtils.getStringRes(R.string.app_name, this, LANG_EN));

                botResponses.clear();
                chatbotAdapter.setAdapter(botResponses);
                chatbotAdapter.notifyDataSetChanged();

                activitySpawnBotBinding.recyclerContainer.setVisibility(View.GONE);
                activitySpawnBotBinding.textviewAnimation.setVisibility(View.VISIBLE);

                updateLanguageConfig(LANG_EN);
                // WebServiceUtils.getInstance().setLanguage("en");
                setUpQuestionsView(LANG_EN);
                BotUtils.getInstance().buildInterpreter(this, LANG_EN);
            }
        }

    }

    private void updateLanguageConfig(String lang) {
        locale = new Locale(lang);
        Configuration overrideConfiguration = getApplicationContext().getResources().getConfiguration();
        overrideConfiguration.setLocale(locale);
        createConfigurationContext(overrideConfiguration);
    }

    public void setUpVolumeButton() {
        if (activitySpawnBotBinding.volumeDown.getVisibility() == View.GONE) {
            activitySpawnBotBinding.volumeDown.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.volumeUp.setVisibility(View.GONE);
            SharedPreferenceUtility.getInstance(this).storePreference(SPEAK, false);
            if (chatMessage != null)
                chatMessage.setSpeakFinish(true);
            notifyBotError();
        } else {
            activitySpawnBotBinding.volumeUp.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.volumeDown.setVisibility(View.GONE);
            SharedPreferenceUtility.getInstance(this).storePreference(SPEAK, true);
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
            FirebaseCrashlytics.getInstance().setCustomKey(ACTION, "App close");
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(Objects.requireNonNull(e.getMessage()));
        }

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
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "10000");
        FirebaseCrashlytics.getInstance().setCustomKey(ACTION, "App Speaking");
    }

    @Override
    public void setAction(String action, Object object) {
        Handler handler = new Handler();
        if (action.equals(AppConstants.WEB_ACTION)) {
            FirebaseCrashlytics.getInstance().setCustomKey(ACTION, "Web Open");
            Intent intent = new Intent(this, SpawnWebActivity.class);
            if (object instanceof SpawnWikiModel)
                intent.putExtra("url", ((SpawnWikiModel) object).getContent_urls().getMobile().getPage());
            else if (object instanceof ValueResults)
                if (((ValueResults) object).getAmpUrl() != null)
                    intent.putExtra("url", ((ValueResults) object).getAmpUrl());
                else intent.putExtra("url", ((ValueResults) object).getUrl());
            startActivity(intent);
        } else if (action.equals(AppConstants.FINISH)) {
            handler.postDelayed(this::finish, 1500);

        } else if (action.equals(SPEAK)) {
            FirebaseCrashlytics.getInstance().setCustomKey(ACTION, "Context conversation");
            handler.postDelayed(this::startListen, 2500);

        } else if (action.equalsIgnoreCase(AppConstants.GOOGLE_SEARCH)) {
            FirebaseCrashlytics.getInstance().setCustomKey(ACTION, "Google Search");
            Intent intent = new Intent(this, SpawnWebActivity.class);
            intent.putExtra("url", getResources().getString(R.string.google_search) + spokenString);
            startActivity(intent);
        } else if (action.equalsIgnoreCase(AppConstants.RESULT_TYPE_NEWS)) {
            try {
                classifyViewModel
                        .getWebSearch(spokenString,
                                SharedPreferenceUtility.getInstance(this).getStringPreference(LANG),
                                AppConstants.RESULT_TYPE_NEWS)
                        .observe(this,
                                (chatCardModel ->
                                        chatViews(null,
                                                chatCardModel.getType(),
                                                chatCardModel)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setChatMessage(ChatMessageType chatMessage) {
        this.chatMessage = chatMessage;
    }

    /**
     * Refreshes the Chat UI. Adds the chat message to botResponse
     *
     * @param chatMessage type of the message to display to user
     */
    private void refreshChat(ChatMessageType chatMessage) {
        if (botResponses.get(botResponses.size() - 1).getViewType() == 2)
            botResponses.remove(botResponses.size() - 1);
        botResponses.add(chatMessage);
        chatbotAdapter.setAdapter(botResponses);
        chatbotAdapter.notifyDataSetChanged();
        activitySpawnBotBinding.chatRecycler.scrollToPosition(chatbotAdapter.getItemCount() - 1);
    }

    /**
     * Start listening for user speech
     */
    private void startListen() {
        if (SpeechRecognizer.isRecognitionAvailable(this) && isSpeechEnabled) {
            chatbotAdapter.setAdapter(botResponses);
            if (speechRecognizer == null)
                initSpeech();
            speechRecognizer.startListening(speechIntentDispatcher);
            activitySpawnBotBinding.micImage.setVisibility(View.GONE);
            activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.mic.playAnimation();
        }
    }


    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            String lang = SharedPreferenceUtility.getInstance(this).getStringPreference(LANG);
            textToSpeech.setLanguage(locale);
            FirebaseCrashlytics.getInstance().setCustomKey("TTSLanguage", lang);
            textToSpeech.setPitch(0.99f);
            textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
        } else {
            Log.e(this.getClass().getName(), "Initilization Failed!");
            FirebaseCrashlytics.getInstance().setCustomKey("ttsInitialization", "Failure");
        }
    }

    private final UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {

        }

        @Override
        public void onDone(String s) {
            runOnUiThread(() -> activitySpawnBotBinding.containerStop.setVisibility(View.GONE));

        }

        @Override
        public void onError(String s) {
            runOnUiThread(() -> activitySpawnBotBinding.containerStop.setVisibility(View.GONE));
        }
    };

    @SuppressLint("NewApi")
    private void registerConnectivityNetworkMonitorForAPI21AndUp() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        Objects.requireNonNull(connectivityManager).registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        sendBroadcast(getConnectivityIntent(false));
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        sendBroadcast(getConnectivityIntent(true));
                    }
                }
        );
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.spawn.ai.CONNECTIVITY_CHANGE".equals(Objects.requireNonNull(intent.getAction()))) {
                boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                if (!isConnected) {
                    activitySpawnBotBinding.micRl.setVisibility(View.VISIBLE);
                } else {
                    activitySpawnBotBinding.micRl.setVisibility(View.GONE);
                }
            }
        }
    };

    private Intent getConnectivityIntent(boolean noConnection) {
        Intent intent = new Intent();
        intent.setAction("com.spawn.ai.CONNECTIVITY_CHANGE");
        intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, noConnection);
        return intent;
    }

    private void onChanged(JSONObject jsonObject) {
        if (jsonObject != null) {
            onSuccess(jsonObject);
        } else onFailure();
    }
}
