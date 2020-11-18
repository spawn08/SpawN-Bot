package com.spawn.ai.interfaces;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface SensexActiveService {

    @GET("mostActiveMonthly.json")
    Observable<Void> getMonthlyActiveStocks();

    @GET("online52NewHigh.json")
    Observable<Void> getYearHighStocks();

    @GET("online52NewLow.json")
    Observable<Void> getYearLowStocks();
}
