package com.spawn.ai.utils.async;

import android.util.Log;

import com.spawn.ai.interfaces.ISpawnAPI;
import com.spawn.ai.model.BotMLResponse;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.network.NLPInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DumpTask implements Runnable {
    private final Object object;
    private final String username;
    private final String password;
    private final String url;

    public DumpTask(Object object, String username, String password, String url) {
        this.object = object;
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @Override
    public void run() {
        //
        if (object instanceof SpawnWikiModel) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NLPInterceptor(username, password))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);
            Call<Object> data = spawnAPI.postData((SpawnWikiModel) object);
            data.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful())
                        Log.d("WIKI-POST -->", "Successfully Dump");
                    else
                        Log.d("WIKI-POST -->", "UnSuccessfully Dump");
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d("WIKI-POST -->", "UnSuccessfully Dump");
                }
            });
        } else if (object instanceof BotMLResponse) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NLPInterceptor(username, password))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final ISpawnAPI spawnAPI = retrofit.create(ISpawnAPI.class);
            Call<Object> data = spawnAPI.postData((BotMLResponse) object);
            data.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful())
                        Log.d("WIKI-POST -->", "Successfully Dump");
                    else
                        Log.d("WIKI-POST -->", "UnSuccessfully Dump");
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d("WIKI-POST -->", "UnSuccessfully Dump");
                }
            });
        }
    }
}


