/*
package com.spawn.ai.network;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spawn.ai.interfaces.SpawnAPIService;
import com.spawn.ai.model.BotMLResponse;
import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.utils.AppUtils;
import com.spawn.ai.utils.JsonFileReader;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceUtils {


    private static final String BOT_URL = "https://api.wit.ai";

    private static WebServiceUtils webServiceUtils;

    private WebServiceUtils() {

    }

    public static WebServiceUtils getInstance() {
        if (webServiceUtils == null) {
            webServiceUtils = new WebServiceUtils();
        }
        return webServiceUtils;
    }

    private void callWitService(final String q) {
        iBotObserver.loading();
        final IBotWebService iBotWebService = retrofit.create(IBotWebService.class);
        final Call<BotResponse> botIntentsCall = iBotWebService.getBotResponse(q);
        botIntentsCall.enqueue(new Callback<BotResponse>() {
            @Override
            public void onResponse(Call<BotResponse> call, Response<BotResponse> response) {
                if (response.isSuccessful()) {
                    ChatCardModel chatCardModel = null;
                    BotResponse botResponse = response.body();
                    if (botResponse != null
                            && botResponse.getEntities().getBotIntents() != null
                            && botResponse.getEntities().getBotIntents().size() > 0
                            && botResponse.getEntities().getBotIntents().get(0).getConfidence() > 0.90)
                        chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getEntities().getBotIntents().get(0).getValue(), 4, language);
                    else if (botResponse != null
                            && botResponse.getEntities().getNotable_person() != null
                            && botResponse.getEntities().getNotable_person().get(0).getValue().getName() != null) {
                        callWikiAPI(botResponse.getEntities().getNotable_person().get(0).getValue().getName(), q);
                    } else if (botResponse != null
                            && botResponse.getEntities().getLocation() != null
                            && botResponse.getEntities().getLocation().get(0).getValue() != null) {
                        callWikiAPI(botResponse.getEntities().getLocation().get(0).getValue(), q);
                    } else if (botResponse != null
                            && botResponse.getEntities().getBotIntents() != null
                            && botResponse.getEntities().getBotIntents().size() > 0)
                        chatCardModel = JsonFileReader.getInstance().getJsonFromKey(botResponse.getEntities().getBotIntents().get(0).getValue(), 4, language);

                    else if (botResponse.getEntities() == null
                            || botResponse.getEntities().getNotable_person() == null
                            || botResponse.getEntities().getLocation() == null) {
                        callWikiAPI(q, q);
                    } else {
                        chatCardModel = new ChatCardModel("", JsonFileReader.getInstance().getDefaultAnswer(language), 1, "");
                    }

                    if (chatCardModel != null)
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
    }

    private void callNewsAPI(final BotMLResponse botResponse) {
        iBotObserver.loading();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NLPInterceptor("escreds0", "escreds1"))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("newsUrl")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final SpawnAPIService iSpawnAPI = retrofit.create(SpawnAPIService.class);
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
}
*/
