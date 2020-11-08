package com.spawn.ai.interfaces;

import com.spawn.ai.model.websearch.News;
import com.spawn.ai.model.websearch.WebSearchResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AzureService {

    @GET("search?mkt=en-IN")
    Call<WebSearchResults> getWebResults(@Query("q") String q, @Query("count") String count);

    @GET("news/search?sortby=date&mkt=en-IN")
    Call<News> getNewsResult(@Query("q") String q, @Query("count") String count);
}
