package com.spawn.ai.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.spawn.ai.constants.AppConstants;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.interfaces.AzureService;
import com.spawn.ai.interfaces.SpawnAPIService;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.utils.task_utils.BotUtils;
import com.spawn.ai.utils.task_utils.JsonFileReader;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClassifyIntentViewModel extends AndroidViewModel {

    private Application application;
    private MutableLiveData<JSONObject> results;
    private MutableLiveData<ChatCardModel> chatCardModelMutableLiveData;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public ClassifyIntentViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public LiveData<JSONObject> classify(String sentence, String language) {
        results = new MutableLiveData<>();
        BotUtils.getInstance().buildInterpreter(application, language);
        return BotUtils.getInstance().classify(sentence, language, results);
    }

    public LiveData<ChatCardModel> getWebSearch(String q, String language, String type, AzureService azureService) throws UnsupportedEncodingException {
        chatCardModelMutableLiveData = new MutableLiveData<>();
        if (type.equalsIgnoreCase("search")) {
            compositeDisposable.add(azureService
                    .getWebResults(URLEncoder.encode(q, "UTF-8"), "5")
                    .subscribeOn(Schedulers.io())
                    .subscribe(webSearchResults -> {
                        ChatCardModel chatCardModel = new ChatCardModel(webSearchResults, ChatViewTypes.CHAT_VIEW_WEB);
                        chatCardModel.setMessage("Here are some results: ");
                        chatCardModel.setLang(language);
                        chatCardModelMutableLiveData.postValue(chatCardModel);
                    }, e -> {
                        ChatCardModel fallBack = JsonFileReader
                                .getInstance()
                                .getJsonFromKey(AppConstants.FALL_BACK,
                                        ChatViewTypes.CHAT_VIEW_DEFAULT,
                                        language);
                        chatCardModelMutableLiveData.postValue(fallBack);
                    }));
        } else if (type.equalsIgnoreCase("news")) {
            compositeDisposable.add(azureService.getNewsResult(q, "10")
                    .subscribeOn(Schedulers.io())
                    .subscribe(news -> {
                        ChatCardModel chatCardModel = new ChatCardModel(news, ChatViewTypes.CHAT_VIEW_NEWS);
                        chatCardModel.setMessage("Here are the latest news: ");
                        chatCardModel.setLang(language);
                        chatCardModelMutableLiveData.postValue(chatCardModel);
                    }, e -> {
                        ChatCardModel fallBack = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK,
                                ChatViewTypes.CHAT_VIEW_DEFAULT,
                                language);
                        chatCardModelMutableLiveData.postValue(fallBack);
                    }));
        }
        return chatCardModelMutableLiveData;
    }

    public LiveData<ChatCardModel> getWikiResponse(String entity, String language) {
        chatCardModelMutableLiveData = new MutableLiveData<>();
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
                .subscribe(
                        spawnWikiModel -> {
                            ChatCardModel chatCardModel;
                            Log.d("API CONTENT: ", spawnWikiModel.toString());

                            if (spawnWikiModel.getType().equals("disambiguation")) {
                                chatCardModelMutableLiveData.postValue(null);
                            } else {
                                chatCardModel = new ChatCardModel(spawnWikiModel, 5);
                                chatCardModelMutableLiveData.postValue(chatCardModel);
                            }
                        }, e -> {
                            ChatCardModel chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                            chatCardModelMutableLiveData.postValue(chatCardModel);
                            FirebaseCrashlytics.getInstance().log("Webservice Request Error -->" + e.getMessage());

                        }
                ));
        return chatCardModelMutableLiveData;
    }
}
