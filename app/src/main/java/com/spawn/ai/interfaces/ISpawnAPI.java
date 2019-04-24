package com.spawn.ai.interfaces;

import com.google.gson.JsonElement;
import com.spawn.ai.model.BotMLResponse;
import com.spawn.ai.model.BotResponse;
import com.spawn.ai.model.SpawnEntityModel;
import com.spawn.ai.model.SpawnWikiModel;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by amarthakur on 11/02/19.
 */

public interface ISpawnAPI {

    @GET("https://en.wikipedia.org/api/rest_v1/page/summary/{entity}")
    Call<SpawnWikiModel> getWiki(@Path("entity") String entity);

    @GET("entity?")
    Call<List<SpawnEntityModel>> getEntity(@Query("q") String query);

    @POST("post_wiki")
    Call<Object> postData(@Body Object spawnWikiModel);

    @GET("api/classify")
    Call<BotResponse> getIntent(@Query("model_name") String model_name, @Query("query") String query);

    @GET("api/getFile")
    Call<JsonElement> getFile(@Query("fileName") String fileName);

    @GET("api/classify")
    Call<BotMLResponse> getIntentTensor(@Query("model") String model, @Query("project") String project, @Query("q") String q);

}
