package com.spawn.ai.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.spawn.ai.SpawnBotActivity;
import com.spawn.ai.interfaces.IBotObserver;
import com.spawn.ai.interfaces.IBotWikiNLP;
import com.spawn.ai.interfaces.ISpawnAPI;
import com.spawn.ai.model.BotMLResponse;
import com.spawn.ai.model.BotResponse;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.SpawnEntityModel;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.utils.AppUtils;
import com.spawn.ai.utils.JsonFileReader;
import com.spawn.ai.utils.SharedPreferenceUtility;
import com.spawn.ai.utils.async.DumpTask;
import com.spawn.ai.utils.async.FireCalls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import constants.AppConstants;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceUtils {

    private static WebServiceUtils webServiceUtils;
    private Retrofit retrofit;
    private static final String BOT_URL = "https://api.wit.ai";
    public static String API_URL = "https://en.wikipedia.org/api/rest_v1/page/summary/";
    public static String NEWS_URL = "https://api.spawnai.com/spawnai_file/news/news_data/";
    public static String SPAWN_API = "https://spawnai.com/";
    private static IBotObserver iBotObserver;
    private IBotWikiNLP iBotWikiNLP;
    private String token;
    private String language;

    @SerializedName("serverFileContents")
    private JsonElement serverFileContents;

    public static WebServiceUtils getInstance(Context context) {
        if (webServiceUtils == null) {
            webServiceUtils = new WebServiceUtils();
        }
        return webServiceUtils;
    }

    public void setUpObserver(Context context) {
        this.iBotObserver = (IBotObserver) context;
        this.iBotWikiNLP = (IBotWikiNLP) context;
    }

    public Retrofit getRetrofitClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new BotInterceptor(token))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BOT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void getBotResponse(String q) {

        if (retrofit != null) {
            //  if (q.split(" ").length > 2)
            callSpawnML(q);
            // else callWikiAPI(q);

        } else {
            retrofit = getRetrofitClient();
            // if (q.split(" ").length > 2)
            callSpawnML(q);
            // else callWikiAPI(q);
        }
    }

    /*public void callWebservice(String q) {
        iBotObserver.loading();
        final IBotWebService iBotWebService = retrofit.create(IBotWebService.class);
        final Call<BotResponse> botIntentsCall = iBotWebService.getBotResponse(q);
        botIntentsCall.enqueue(new Callback<BotResponse>() {
            @Override
            public void onResponse(Call<BotResponse> call, Response<BotResponse> response) {
                if (response.isSuccessful()) {
                    ChatCardModel chatCardModel = null;
                    BotResponse botResponse = response.body();
                    if (botResponse.getEntities().getBotIntents() != null &&
                            botResponse.getEntities().getBotIntents().size() > 0)
                        chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getEntities().getBotIntents().get(0).getName(), 4);
                    else {
                        chatCardModel = new ChatCardModel("", JsonFileReader.getInstance().getDefaultAnswer(), 1, "");
                    }
                    iBotObserver.notifyBotResponse(chatCardModel);
                } else {
                    iBotObserver.notifyBotError();
                }
            }

            @Override
            public void onFailure(Call<BotResponse> call, Throwable t) {
                iBotObserver.notifyBotError();
            }
        });
    }*/

    public void callSpawnML(final String q) {
        try {
            iBotObserver.loading();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NLPInterceptor(AppConstants.NLP_USERNAME, AppConstants.NLP_PASSWORD))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SPAWN_API)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);
            Call<BotMLResponse> data = spawnAPI.getEntityExtract(q, AppConstants.MODEL, language);

            data.enqueue(new Callback<BotMLResponse>() {
                @Override
                public void onResponse(Call<BotMLResponse> call, Response<BotMLResponse> response) {
                    if (response.isSuccessful()) {
                        ChatCardModel chatCardModel = null;
                        BotMLResponse botResponse = response.body();
                        botResponse.setLang(language);
                        FireCalls.exec(new DumpTask(botResponse));

                        if (botResponse.getEntities().size() > 0 && botResponse.getEntities().get(0).getEntity() != null && !botResponse.getEntities().get(0).getEntity().isEmpty()) {
                            callWikiAPI(botResponse.getEntities().get(0).getEntity(), q);
                        } else if (botResponse.getIntent().getName() != null &&
                                !botResponse.getIntent().getName().isEmpty() &&
                                botResponse.getIntent().getName().equalsIgnoreCase("bot_news")
                                && botResponse.getIntent().getConfidence() > 0.80) {
                            callNewsAPI(botResponse);

                        } else if (botResponse.getIntent().getName() != null &&
                                !botResponse.getIntent().getName().isEmpty() && botResponse.getIntent().getConfidence() > 0.80) {
                            chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getIntent().getName(), 4, language);
                            iBotObserver.notifyBotResponse(chatCardModel);
                        } else {
                            if (botResponse.getEntities().size() > 0 && botResponse.getEntities().get(0).getValue() != null)
                                callWikiAPI(botResponse.getEntities().get(0).getValue().get(0), q);
                            else {
                                if (botResponse.getIntent().getName() != null &&
                                        !botResponse.getIntent().getName().isEmpty() && botResponse.getIntent().getConfidence() > 0.65) {
                                    chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getIntent().getName(), 4, language);
                                    iBotObserver.notifyBotResponse(chatCardModel);
                                } else {
                                    /*ChatCardModel fallbackModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4);
                                    iBotObserver.notifyBotResponse(fallbackModel);*/
                                    String[] splitQuery = botResponse.getText().split(" ");
                                    if (splitQuery.length < 3)
                                        callWikiAPI(botResponse.getText(), q);
                                    else {
                                        ChatCardModel fallbackModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                                        iBotObserver.notifyBotResponse(fallbackModel);
                                    }
                                }
                            }
                        }

                    } else {
                        callSpawnAPI(q);
                    }
                }

                @Override
                public void onFailure(Call<BotMLResponse> call, Throwable t) {
                    callWikiAPI(q, q);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void callNewsAPI(final BotMLResponse botResponse) {
        iBotObserver.loading();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NLPInterceptor(AppConstants.NEWS_USERNAME, AppConstants.NEWS_PASS))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NEWS_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ISpawnAPI iSpawnAPI = retrofit.create(ISpawnAPI.class);
        Call<JsonElement> data = iSpawnAPI.getNewsData();

        data.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                try {

                    JsonObject resp = response.body().getAsJsonObject();
                    JSONObject source = new JSONObject(resp.toString());
                    JSONArray jsonArray = source.getJSONObject("_source").getJSONArray("result");
                    AppUtils.getInstance().setNewsJSON(jsonArray);
                    ChatCardModel chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getIntent().getName(), 4, language);
                    iBotObserver.notifyBotResponse(chatCardModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.d(WebServiceUtils.class.getSimpleName(), t.getMessage());
            }
        });


    }


    public void callSpawnAPI(final String query) {
        iBotObserver.loading();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NLPInterceptor(AppConstants.NLP_USERNAME, AppConstants.NLP_PASSWORD))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SPAWN_API)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);
        Call<List<SpawnEntityModel>> data = spawnAPI.getEntity(query);

        data.enqueue(new Callback<List<SpawnEntityModel>>() {
            @Override
            public void onResponse(Call<List<SpawnEntityModel>> call, Response<List<SpawnEntityModel>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<SpawnEntityModel> model = response.body();
                        callWikiAPI(model.get(0).getValue(), query);
                    } else {
                        ChatCardModel chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                        iBotObserver.notifyBotResponse(chatCardModel);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.log(1, "Webservice -->", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<SpawnEntityModel>> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                ChatCardModel chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                iBotObserver.notifyBotResponse(chatCardModel);

            }
        });
    }

    public void callWikiAPI(final String entity, final String query) {
        iBotObserver.loading();
        final String cloneEntity = entity.trim().replace(" ", "_");

        Log.d("ENTITY: ", cloneEntity);
        String urlKey = "api_url_" + language;
        API_URL = JsonFileReader.getInstance().getValueFromJson(urlKey);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Call<SpawnWikiModel> data = null;
        final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);

        if (language.equalsIgnoreCase("en"))
            data = spawnAPI.getWiki(cloneEntity);
        else
            data = spawnAPI.getWikiHI(cloneEntity);

        data.enqueue(new Callback<SpawnWikiModel>() {
            @Override
            public void onResponse(Call<SpawnWikiModel> call, Response<SpawnWikiModel> response) {
                if (response.isSuccessful()) {
                    ChatCardModel chatCardModel = null;
                    Log.d("API CONTENT: ", response.body().toString());
                    SpawnWikiModel spawnWikiModel = response.body();
                    //spawnWikiModel.setQuery(query);
                    FireCalls.exec(new DumpTask(spawnWikiModel));
                    if (spawnWikiModel.getType().equals("disambiguation")) {
                        //Handle case for page not found
                        String[] disambiguationSplit = spawnWikiModel.getExtract().split(":");
                        if (disambiguationSplit.length > 1 && disambiguationSplit[1].length() > 10) {
                            spawnWikiModel.setDescription(disambiguationSplit[1]);
                            chatCardModel = new ChatCardModel(spawnWikiModel, 5);
                            iBotWikiNLP.showUI(chatCardModel);
                        } else {
                            chatCardModel = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                            iBotObserver.notifyBotResponse(chatCardModel);
                        }
                    } else {
                        chatCardModel = new ChatCardModel(spawnWikiModel, 5);
                        iBotWikiNLP.showUI(chatCardModel);
                    }

                } else {
                    //Handle case for page not found
                    ChatCardModel fallBack = JsonFileReader.getInstance().getJsonFromKey(AppConstants.FALL_BACK, 4, language);
                    iBotObserver.notifyBotResponse(fallBack);
                }
            }

            @Override
            public void onFailure(Call<SpawnWikiModel> call, Throwable t) {
                //Handle case for failure
                Crashlytics.log(1, "Webservice Request Error -->", t.getMessage());

            }
        });

    }

    public void getFile(String fileName, final Activity activity) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NLPInterceptor(AppConstants.NLP_USERNAME, AppConstants.NLP_PASSWORD))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SPAWN_API)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);
            Call<JsonElement> data = spawnAPI.getFile(fileName);

            data.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    try {
                        if (response.isSuccessful()) {
                            JsonElement file = response.body();
                            setFileContents(file);
                            if (file != null) {
                                JsonFileReader.getInstance().fileName(AppConstants.DATA_FILE);
                                JsonFileReader.getInstance().readFile(activity, file);
                                JsonFileReader.getInstance().setQuestions(SharedPreferenceUtility.getInstance(activity).getStringPreference("lang"));
                            }
                            Intent intent = new Intent(activity, SpawnBotActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.log(1, "Webservice -->", e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Log.e("ERROR: ", t.getMessage());
                    Crashlytics.log(1, "Webservice Request Error -->", t.getMessage());
                    JsonFileReader.getInstance().readFile(activity, null);
                    Intent intent = new Intent(activity, SpawnBotActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.log(1, "Webservice", e.getMessage());
        }
    }

    public JsonElement getFileContents() {
        return serverFileContents;
    }

    public void setFileContents(JsonElement fileContents) {
        this.serverFileContents = fileContents;
    }
}
