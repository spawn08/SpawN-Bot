package com.spawn.ai.interfaces;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface SensexService {

    @GET("gainers/niftyGainers1.json")
    Observable<Void> getTopGainers();

    @GET("losers/niftyLosers1.json")
    Observable<Void> getTopLosers();

    @GET("gainers/fnoGainers1.json")
    Observable<Void> getFnoGainers();

    @GET("losers/fnoLosers1.json")
    Observable<Void> getFnoLosers();

    @GET("pre_open/nifty.json")
    Observable<Void> getPreopenNiftyStock();

    @GET("pre_open/fo.json")
    Observable<Void> getPreopenFnoStock();

    @GET("pre_open/niftybank.json")
    Observable<Void> getPreopenNiftyBank();

}

