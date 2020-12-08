package com.spawn.ai.interfaces;

import com.google.gson.JsonElement;
import com.spawn.ai.model.BotMLResponse;
import com.spawn.ai.model.BotResponse;
import com.spawn.ai.model.SpawnEntityModel;
import com.spawn.ai.model.wiki.SpawnWikiModel;
import com.spawn.ai.model.websearch.News;
import com.spawn.ai.model.websearch.WebSearchResults;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by amarthakur on 11/02/19.
 */

public interface SpawnAPIService {

    @GET("https://en.wikipedia.org/api/rest_v1/page/summary/{entity}")
    Observable<SpawnWikiModel> getWiki(@Path("entity") String entity);

    @GET("https://api.spawnai.com/spawnai_file/news/news_data")
    Call<JsonElement> getNewsData();

    @GET("https://hi.wikipedia.org/api/rest_v1/page/summary/{entity}")
    Observable<SpawnWikiModel> getWikiHI(@Path("entity") String entity);

    @GET("entity?")
    Call<List<SpawnEntityModel>> getEntity(@Query("q") String query);

    @POST("post_wiki")
    Call<Object> postData(@Body Object spawnWikiModel);

    @GET("api/classify")
    Call<BotResponse> getIntent(@Query("model_name") String model_name, @Query("query") String query);

    @GET("api/getFile")
    Observable<JsonElement> getFile(@Query("fileName") String fileName);

    @GET("api/classify")
    Call<BotMLResponse> getIntentTensor(@Query("model") String model, @Query("project") String project, @Query("q") String q);

    @GET("entity_extract")
    Observable<BotMLResponse> getEntityExtract(@Query("q") String q, @Query("model") String model, @Query("lang") String lang);

    @GET("websearch")
    Observable<WebSearchResults> getWebResults(@Query("q") String q, @Query("count") String count, @Query("result_type") String resultType);

    @GET("websearch")
    Observable<News> getNewsResult(@Query("q") String q, @Query("count") String count, @Query("result_type") String resultType);

}
