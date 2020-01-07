package com.spawn.ai.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.interfaces.ISpawnAPI;
import com.spawn.ai.model.BotMLResponse;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.model.websearch.News;
import com.spawn.ai.model.websearch.WebSearchResults;
import com.spawn.ai.network.NLPInterceptor;
import com.spawn.ai.network.WebServiceUtils;
import com.spawn.ai.utils.task_utils.JsonFileReader;

import constants.AppConstants;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebSearchViewModel extends ViewModel {

    private MutableLiveData<ChatCardModel> chatCardModelMutableLiveData;
    private String[] creds = WebServiceUtils.getInstance().getAPICreds().split(":");
    private String apiUrl = WebServiceUtils.getInstance().getUrl();

    private void getMLResponse(String q, String type, String language) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NLPInterceptor(creds[0], creds[1]))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);
        if (type.equalsIgnoreCase("search")) {
            Call<WebSearchResults> data = spawnAPI.getWebResults(q, "5", type);
            data.enqueue(new Callback<WebSearchResults>() {
                @Override
                public void onResponse(Call<WebSearchResults> call, Response<WebSearchResults> response) {
                    if (response.isSuccessful()) {
                        Log.e("RESULT-->", response.body().toString());
                        ChatCardModel chatCardModel = new ChatCardModel(response.body(), ChatViewTypes.CHAT_VIEW_WEB);
                        chatCardModel.setMessage("Here are some results: ");
                        chatCardModel.setLang(language);
                        chatCardModelMutableLiveData.setValue(chatCardModel);
                    } else {
                        ChatCardModel fallBack = JsonFileReader
                                .getInstance()
                                .getJsonFromKey(AppConstants.FALL_BACK,
                                        ChatViewTypes.CHAT_VIEW_DEFAULT,
                                        language);
                        chatCardModelMutableLiveData.setValue(fallBack);

                    }
                }

                @Override
                public void onFailure(Call<WebSearchResults> call, Throwable t) {
                    ChatCardModel fallBack = JsonFileReader
                            .getInstance()
                            .getJsonFromKey(AppConstants.FALL_BACK,
                                    ChatViewTypes.CHAT_VIEW_DEFAULT,
                                    language);
                    chatCardModelMutableLiveData.setValue(fallBack);
                }
            });
        } else if (type.equalsIgnoreCase("news")) {
            Call<News> data = spawnAPI.getNewsResult(q, "10", type);
            data.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    if (response.isSuccessful()) {
                        Log.e("RESULT-->", response.body().toString());
                        ChatCardModel chatCardModel = new ChatCardModel(response.body(), ChatViewTypes.CHAT_VIEW_NEWS);
                        chatCardModel.setMessage("Here are the latest news: ");
                        chatCardModel.setLang(language);
                        chatCardModelMutableLiveData.setValue(chatCardModel);
                    } else {
                        ChatCardModel fallBack = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK,
                                ChatViewTypes.CHAT_VIEW_DEFAULT,
                                language);
                        chatCardModelMutableLiveData.setValue(fallBack);
                    }
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    ChatCardModel fallBack = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                    chatCardModelMutableLiveData.setValue(fallBack);
                }
            });
        }
    }

    private void getWikiResponse(String entity, final String query, String language) {
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
                if (response != null && response.isSuccessful()) {
                    ChatCardModel chatCardModel;
                    Log.d("API CONTENT: ", response.body().toString());
                    SpawnWikiModel spawnWikiModel = response.body();

                    //FireCalls.exec(new DumpTask(spawnWikiModel));
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

                } else {

                    ChatCardModel chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                    chatCardModelMutableLiveData.setValue(chatCardModel);
                }
            }

            @Override
            public void onFailure(Call<SpawnWikiModel> call, Throwable t) {
                //Handle case for failure
                ChatCardModel chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                chatCardModelMutableLiveData.setValue(chatCardModel);
                Crashlytics.log(1, "Webservice Request Error -->", t.getMessage());

            }
        });

    }

    public LiveData<ChatCardModel> getSpawnAIResponse(String q, String language) {
        chatCardModelMutableLiveData = new MutableLiveData<>();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NLPInterceptor(creds[0], creds[1]))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);
        Call<BotMLResponse> data = spawnAPI.getEntityExtract(q, AppConstants.MODEL, language);

        data.enqueue(new Callback<BotMLResponse>() {
            @Override
            public void onResponse(Call<BotMLResponse> call, Response<BotMLResponse> response) {
                if (response.isSuccessful()) {
                    ChatCardModel chatCardModel;
                    BotMLResponse botResponse = response.body();
                    botResponse.setLang(language);
                    // FireCalls.exec(new DumpTask(botResponse));

                    if (botResponse.getEntities().size() > 0
                            && botResponse.getEntities().get(0).getEntity() != null
                            && !botResponse.getEntities().get(0).getEntity().isEmpty()
                            /*&& botResponse.getIntent().getName() != null
                            && !botResponse.getIntent().getName().equalsIgnoreCase("bot_news")*/) {
                        getWikiResponse(botResponse.getEntities().get(0).getEntity(), q, language);
                    } else if (botResponse.getIntent().getName() != null &&
                            !botResponse.getIntent().getName().isEmpty() &&
                            botResponse.getIntent().getName().equalsIgnoreCase("bot_news")
                            && botResponse.getIntent().getConfidence() > 0.80) {
                        getMLResponse(q, AppConstants.RESULT_TYPE_NEWS, language);

                    } else if (botResponse.getIntent().getName() != null &&
                            !botResponse.getIntent().getName().isEmpty() && botResponse.getIntent().getConfidence() > 0.80) {
                        chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getIntent().getName(), 4, language);
                        chatCardModelMutableLiveData.setValue(chatCardModel);
                    } else {
                        if (botResponse.getEntities().size() > 0 && botResponse.getEntities().get(0).getValue() != null)
                            //callWikiAPI(botResponse.getEntities().get(0).getEntity(), q);
                            getWikiResponse(q, AppConstants.RESULT_TYPE_SEARCH, language);
                        else {
                            if (botResponse.getIntent().getName() != null &&
                                    !botResponse.getIntent().getName().isEmpty() && botResponse.getIntent().getConfidence() > 0.65) {
                                chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getIntent().getName(), 4, language);
                                chatCardModelMutableLiveData.setValue(chatCardModel);
                            } else {
                                String[] splitQuery = botResponse.getText().split(" ");
                                if (splitQuery.length < 3)
                                    getMLResponse(botResponse.getText(), AppConstants.RESULT_TYPE_SEARCH, language);
                                else {
                                    getMLResponse(botResponse.getText(), AppConstants.RESULT_TYPE_SEARCH, language);
                                }
                            }
                        }
                    }

                } else {
                    getMLResponse(q, AppConstants.RESULT_TYPE_SEARCH, language);
                }
            }

            @Override
            public void onFailure(Call<BotMLResponse> call, Throwable t) {
                ChatCardModel fallBack = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK,
                        ChatViewTypes.CHAT_VIEW_DEFAULT,
                        language);
                chatCardModelMutableLiveData.setValue(fallBack);
            }
        });
        return chatCardModelMutableLiveData;
    }

}
