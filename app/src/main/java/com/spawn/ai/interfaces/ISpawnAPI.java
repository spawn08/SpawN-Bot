package com.spawn.ai.interfaces;

import com.spawn.ai.model.SpawnEntityModel;
import com.spawn.ai.model.SpawnWikiModel;

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
    Call<SpawnWikiModel> postData(@Body SpawnWikiModel spawnWikiModel);

}
