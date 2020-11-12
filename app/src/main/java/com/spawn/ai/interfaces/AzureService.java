package com.spawn.ai.interfaces;

import com.spawn.ai.model.websearch.News;
import com.spawn.ai.model.websearch.WebSearchResults;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AzureService {

    @GET("search?mkt=en-IN")
    Observable<WebSearchResults> getWebResults(@Query("q") String q, @Query("count") String count);

    @GET("news/search?sortby=date&mkt=en-IN")
    Observable<News> getNewsResult(@Query("q") String q, @Query("count") String count);
}
