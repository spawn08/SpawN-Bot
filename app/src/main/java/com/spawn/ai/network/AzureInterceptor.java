package com.spawn.ai.network;

import com.spawn.ai.utils.task_utils.AppUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AzureInterceptor implements Interceptor {

    private AppUtils appUtils;

    public AzureInterceptor(AppUtils appUtils){
        this.appUtils = appUtils;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request azureReq = request.newBuilder().addHeader("Ocp-Apim-Subscription-Key", appUtils.getAzureKey())
                .addHeader("User-Agent", "Android")
                .addHeader("BingAPIs-Market", "en-IN")
                .build();
        return chain.proceed(azureReq);
    }
}
