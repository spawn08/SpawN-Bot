package com.spawn.ai.network;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NLPInterceptor implements Interceptor {

    private String username;
    private String password;

    public NLPInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticate = request.newBuilder()
                .addHeader("Authorization", Credentials.basic(username, password))
                .build();
        return chain.proceed(authenticate);
    }
}
