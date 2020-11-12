package com.spawn.ai.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonElement;
import com.spawn.ai.constants.AppConstants;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.interfaces.SpawnAPIService;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.network.NLPInterceptor;
import com.spawn.ai.utils.task_utils.AppUtils;
import com.spawn.ai.utils.task_utils.JsonFileReader;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebSearchViewModel extends ViewModel {

    private MutableLiveData<ChatCardModel> chatCardModelMutableLiveData;
    private MutableLiveData<JsonElement> jsonElementMutableLiveData;
    private String[] creds = null;
    private String apiUrl = null;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    private void getMLResponse(String q, String type, String language, AppUtils appUtils) {
        creds = appUtils.getAPICreds().split(":");
        apiUrl = appUtils.getUrl();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NLPInterceptor(creds[0], creds[1]))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        final SpawnAPIService spawnAPI = retrofit.create(SpawnAPIService.class);
        if (type.equalsIgnoreCase("search")) {
            compositeDisposable.add(spawnAPI.getWebResults(q, "5", type)
                    .subscribeOn(Schedulers.io())
                    .subscribe(webSearchResults -> {
                        ChatCardModel chatCardModel = new ChatCardModel(webSearchResults, ChatViewTypes.CHAT_VIEW_WEB);
                        chatCardModel.setMessage("Here are some results: ");
                        chatCardModel.setLang(language);
                        chatCardModelMutableLiveData.setValue(chatCardModel);
                    }, e -> {
                        ChatCardModel fallBack = JsonFileReader
                                .getInstance()
                                .getJsonFromKey(AppConstants.FALL_BACK,
                                        ChatViewTypes.CHAT_VIEW_DEFAULT,
                                        language);
                        chatCardModelMutableLiveData.setValue(fallBack);
                    })
            );
        } else if (type.equalsIgnoreCase("news")) {
            compositeDisposable.add(spawnAPI.getNewsResult(q, "10", type)
                    .subscribeOn(Schedulers.io())
                    .subscribe(news -> {
                        ChatCardModel chatCardModel = new ChatCardModel(news, ChatViewTypes.CHAT_VIEW_NEWS);
                        chatCardModel.setMessage("Here are the latest news: ");
                        chatCardModel.setLang(language);
                        chatCardModelMutableLiveData.setValue(chatCardModel);
                    }, e -> {
                        ChatCardModel fallBack = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK,
                                ChatViewTypes.CHAT_VIEW_DEFAULT,
                                language);
                        chatCardModelMutableLiveData.setValue(fallBack);
                    })
            );
        }
    }

    private void getWikiResponse(String entity, String language) {
        final String cloneEntity = entity.trim().replace(" ", "_");

        Log.d("ENTITY: ", cloneEntity);
        String urlKey = "api_url_" + language;
        String API_URL = JsonFileReader.getInstance().getValueFromJson(urlKey);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        Observable<SpawnWikiModel> data;
        final SpawnAPIService spawnAPI = retrofit.create(SpawnAPIService.class);

        if (language.equalsIgnoreCase("en"))
            data = spawnAPI.getWiki(cloneEntity);
        else
            data = spawnAPI.getWikiHI(cloneEntity);

        compositeDisposable.add(data
                .subscribeOn(Schedulers.io())
                .subscribe(spawnWikiModel -> {
                    ChatCardModel chatCardModel;
                    Log.d("API CONTENT: ", spawnWikiModel.toString());
                    if (spawnWikiModel.getType().equals("disambiguation")) {
                        //Handle case for page not found
                        String[] disambiguationSplit = spawnWikiModel.getExtract().split(":");
                        if (disambiguationSplit.length > 1 && disambiguationSplit[1].length() > 10) {
                            spawnWikiModel.setDescription(disambiguationSplit[1]);
                            chatCardModel = new ChatCardModel(spawnWikiModel, 5);
                            chatCardModelMutableLiveData.setValue(chatCardModel);
                        } else {
                            chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                            chatCardModelMutableLiveData.setValue(chatCardModel);
                        }
                    } else {
                        chatCardModel = new ChatCardModel(spawnWikiModel, 5);
                        chatCardModelMutableLiveData.setValue(chatCardModel);
                    }
                }, e -> {
                    ChatCardModel chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                    chatCardModelMutableLiveData.setValue(chatCardModel);
                    FirebaseCrashlytics.getInstance().log("Webservice Request Error -->" + e.getMessage());
                }));
    }

    public LiveData<ChatCardModel> getSpawnAIResponse(String q, String language, AppUtils appUtils) {
        chatCardModelMutableLiveData = new MutableLiveData<>();
        apiUrl = appUtils.getUrl();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NLPInterceptor(creds[0], creds[1]))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        final SpawnAPIService spawnAPI = retrofit.create(SpawnAPIService.class);
        compositeDisposable.add(spawnAPI.getEntityExtract(q, AppConstants.MODEL, language)
                .subscribeOn(Schedulers.io())
                .subscribe(botResponse -> {
                    ChatCardModel chatCardModel;
                    botResponse.setLang(language);

                    if (botResponse.getEntities().size() > 0
                            && botResponse.getEntities().get(0).getEntity() != null
                            && !botResponse.getEntities().get(0).getEntity().isEmpty()
                            /*&& botResponse.getIntent().getName() != null
                            && !botResponse.getIntent().getName().equalsIgnoreCase("bot_news")*/) {
                        getWikiResponse(botResponse.getEntities().get(0).getEntity(), language);
                    } else if (botResponse.getIntent().getName() != null &&
                            !botResponse.getIntent().getName().isEmpty() &&
                            botResponse.getIntent().getName().equalsIgnoreCase("bot_news")
                            && botResponse.getIntent().getConfidence() > 0.80) {
                        getMLResponse(q, AppConstants.RESULT_TYPE_NEWS, language, appUtils);

                    } else if (botResponse.getIntent().getName() != null &&
                            !botResponse.getIntent().getName().isEmpty() && botResponse.getIntent().getConfidence() > 0.80) {
                        chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getIntent().getName(), 4, language);
                        chatCardModelMutableLiveData.setValue(chatCardModel);
                    } else {
                        if (botResponse.getEntities().size() > 0 && botResponse.getEntities().get(0).getValue() != null)
                            getWikiResponse(q, language);
                        else {
                            if (botResponse.getIntent().getName() != null &&
                                    !botResponse.getIntent().getName().isEmpty() && botResponse.getIntent().getConfidence() > 0.65) {
                                chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getIntent().getName(), 4, language);
                                chatCardModelMutableLiveData.setValue(chatCardModel);
                            } else {
                                String[] splitQuery = botResponse.getText().split(" ");
                                if (splitQuery.length < 3)
                                    getMLResponse(botResponse.getText(), AppConstants.RESULT_TYPE_SEARCH, language, appUtils);
                                else {
                                    getMLResponse(botResponse.getText(), AppConstants.RESULT_TYPE_SEARCH, language, appUtils);
                                }
                            }
                        }
                    }
                }, e -> {
                    getMLResponse(q, AppConstants.RESULT_TYPE_SEARCH, language, appUtils);
                })
        );
        return chatCardModelMutableLiveData;
    }

    public LiveData<JsonElement> getFile(String fileName, AppUtils appUtils) {
        try {
            jsonElementMutableLiveData = new MutableLiveData<>();
            apiUrl = appUtils.getUrl();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NLPInterceptor(creds[0], creds[0]))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(apiUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();

            final SpawnAPIService spawnAPI = retrofit.create(SpawnAPIService.class);

            compositeDisposable.add(spawnAPI.getFile(fileName)
                    .subscribeOn(Schedulers.io())
                    .subscribe(jsonElement -> {
                        jsonElementMutableLiveData.postValue(jsonElement);
                    }, e -> {
                        jsonElementMutableLiveData.postValue(null);
                        FirebaseCrashlytics.getInstance().log("Webservice -->" + e.getMessage());
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            jsonElementMutableLiveData.setValue(null);
            FirebaseCrashlytics.getInstance().log("Webservice -> " + e.getMessage());
        }
        return jsonElementMutableLiveData;
    }

}
