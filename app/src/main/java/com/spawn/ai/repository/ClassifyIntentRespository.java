package com.spawn.ai.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.spawn.ai.constants.AppConstants;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.interfaces.AzureService;
import com.spawn.ai.interfaces.SensexActiveService;
import com.spawn.ai.interfaces.SensexService;
import com.spawn.ai.interfaces.SpawnAPIService;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.wiki.SpawnWikiModel;
import com.spawn.ai.utils.BotUtils;
import com.spawn.ai.utils.JsonFileReader;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.spawn.ai.constants.AppConstants.RESULT_TYPE_NEWS;
import static com.spawn.ai.constants.AppConstants.RESULT_TYPE_SEARCH;

@Singleton
public class ClassifyIntentRespository {

    private final BotUtils botUtils;
    private final AzureService azureService;
    private final SensexService sensexService;
    private final SensexActiveService sensexActiveService;
    private MutableLiveData<ChatCardModel> chatCardModelMutableLiveData;
    private final CompositeDisposable compositeDisposable;

    @Inject
    public ClassifyIntentRespository(BotUtils botUtils,AzureService azureService,
                                     SensexService sensexService,
                                     SensexActiveService sensexActiveService,
                                     CompositeDisposable compositeDisposable) {
        this.botUtils = botUtils;
        this.azureService = azureService;
        this.sensexService = sensexService;
        this.sensexActiveService = sensexActiveService;
        this.compositeDisposable = compositeDisposable;
    }

    /**
     * Perform classification task on sentence
     *
     * @param sentence user defined query
     * @param language language of the user query
     * @return LiveData object to observe on
     */
    public LiveData<JSONObject> classify(String sentence, String language) {
        MutableLiveData<JSONObject> results = new MutableLiveData<>();
        compositeDisposable.add(botUtils.classify(sentence, language).subscribe(
                results::setValue,
                throwable -> {
                    results.setValue(null);
                }
        ));
        return results;
    }

    public LiveData<ChatCardModel> getWebSearch(String q, String language, String type) throws UnsupportedEncodingException {
        chatCardModelMutableLiveData = new MutableLiveData<>();
        if (type.equalsIgnoreCase(RESULT_TYPE_SEARCH)) {
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
        } else if (type.equalsIgnoreCase(RESULT_TYPE_NEWS)) {
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

        if (language.equalsIgnoreCase(AppConstants.LANG_EN))
            data = spawnAPI.getWiki(cloneEntity);
        else
            data = spawnAPI.getWikiHI(cloneEntity);

        compositeDisposable.add(data
                .subscribeOn(Schedulers.io())
                .subscribe(
                        spawnWikiModel -> {
                            ChatCardModel chatCardModel;
                            Log.d("API CONTENT: ", spawnWikiModel.toString());

                            if (spawnWikiModel.getType().equals(AppConstants.DISAMBIGUATION)) {
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

    /**
     * Return composite disposable object to dispose off in viewmodel
     *
     * @return CompositeDisposable
     */
    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }
}
