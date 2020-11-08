package com.spawn.ai.viewmodels;

import android.app.Application;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.spawn.ai.constants.AppConstants;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.interfaces.AzureService;
import com.spawn.ai.interfaces.ISpawnAPI;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.model.websearch.News;
import com.spawn.ai.model.websearch.WebSearchResults;
import com.spawn.ai.network.AzureInterceptor;
import com.spawn.ai.utils.task_utils.AppUtils;
import com.spawn.ai.utils.task_utils.BotUtils;
import com.spawn.ai.utils.task_utils.JsonFileReader;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClassifyViewModel extends AndroidViewModel {

    private Application application;
    private MutableLiveData<JSONObject> results;
    private MutableLiveData<ChatCardModel> chatCardModelMutableLiveData;
    private String[] creds = AppUtils.getInstance().getAPICreds().split(":");
    private String apiUrl = AppUtils.getInstance().getUrl();
    private String searchUrl = AppUtils.getInstance().getWebApiUrl();

    public ClassifyViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public MutableLiveData<JSONObject> classify(String sentence, String language) {
        results = new MutableLiveData<>();
        BotUtils.getInstance().buildInterpreter(application, language);
        return BotUtils.getInstance().classify(sentence, language, results);
    }

    public MutableLiveData<ChatCardModel> getWebSearch(String q, String language, String type) throws UnsupportedEncodingException {
        chatCardModelMutableLiveData = new MutableLiveData<>();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AzureInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(searchUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final AzureService azureService = retrofit.create(AzureService.class);
        if (type.equalsIgnoreCase("search")) {
            Call<WebSearchResults> data = azureService.getWebResults(URLEncoder.encode(q, "UTF-8"), "5");
            data.enqueue(new Callback<WebSearchResults>() {
                @Override
                public void onResponse(Call<WebSearchResults> call, Response<WebSearchResults> response) {
                    if (response.isSuccessful()) {

                        ChatCardModel chatCardModel = new ChatCardModel(response.body(), ChatViewTypes.CHAT_VIEW_WEB);
                        chatCardModel.setMessage("Here are some results: ");
                        chatCardModel.setLang(language);
                        chatCardModelMutableLiveData.postValue(chatCardModel);
                    } else {
                        ChatCardModel fallBack = JsonFileReader
                                .getInstance()
                                .getJsonFromKey(AppConstants.FALL_BACK,
                                        ChatViewTypes.CHAT_VIEW_DEFAULT,
                                        language);
                        chatCardModelMutableLiveData.postValue(fallBack);

                    }
                }

                @Override
                public void onFailure(Call<WebSearchResults> call, Throwable t) {
                    ChatCardModel fallBack = JsonFileReader
                            .getInstance()
                            .getJsonFromKey(AppConstants.FALL_BACK,
                                    ChatViewTypes.CHAT_VIEW_DEFAULT,
                                    language);
                    chatCardModelMutableLiveData.postValue(fallBack);
                }
            });
        } else if (type.equalsIgnoreCase("news")) {
            Call<News> data = azureService.getNewsResult(q, "10");
            data.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    if (response.isSuccessful() && !response.body().getValue().isEmpty()) {
                        ChatCardModel chatCardModel = new ChatCardModel(response.body(), ChatViewTypes.CHAT_VIEW_NEWS);
                        chatCardModel.setMessage("Here are the latest news: ");
                        chatCardModel.setLang(language);
                        chatCardModelMutableLiveData.postValue(chatCardModel);
                    } else {
                        ChatCardModel fallBack = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK,
                                ChatViewTypes.CHAT_VIEW_DEFAULT,
                                language);
                        chatCardModelMutableLiveData.postValue(fallBack);
                    }
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    ChatCardModel fallBack = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                    chatCardModelMutableLiveData.postValue(fallBack);
                }
            });
        }
        return chatCardModelMutableLiveData;
    }

    public MutableLiveData<ChatCardModel> getWikiResponse(String entity, final String query, String language) {
        chatCardModelMutableLiveData = new MutableLiveData<>();
        final String cloneEntity = entity.trim().replace(" ", "_");

        Log.d("ENTITY: ", cloneEntity);
        String urlKey = "api_url_" + language;
        String API_URL = JsonFileReader.getInstance().getValueFromJson(urlKey);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Call<SpawnWikiModel> data;
        final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);

        if (language.equalsIgnoreCase("en"))
            data = spawnAPI.getWiki(cloneEntity);
        else
            data = spawnAPI.getWikiHI(cloneEntity);

        data.enqueue(new Callback<SpawnWikiModel>() {
            @Override
            public void onResponse(Call<SpawnWikiModel> call, Response<SpawnWikiModel> response) {
                if (response.isSuccessful()) {
                    ChatCardModel chatCardModel;
                    Log.d("API CONTENT: ", response.body().toString());
                    SpawnWikiModel spawnWikiModel = response.body();

                    //FireCalls.exec(new DumpTask(spawnWikiModel));
                    if (spawnWikiModel.getType().equals("disambiguation")) {
                        //Handle case for page not found
                        chatCardModelMutableLiveData.postValue(null);
                    } else {
                        chatCardModel = new ChatCardModel(spawnWikiModel, 5);
                        chatCardModelMutableLiveData.postValue(chatCardModel);
                    }

                } else {

                    chatCardModelMutableLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<SpawnWikiModel> call, Throwable t) {
                //Handle case for failure
                ChatCardModel chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                chatCardModelMutableLiveData.postValue(chatCardModel);
                FirebaseCrashlytics.getInstance().log("Webservice Request Error -->" + t.getMessage());

            }
        });
        return chatCardModelMutableLiveData;
    }
}
