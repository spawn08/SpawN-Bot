package com.spawn.ai.utils.async;

import android.util.Log;

import com.spawn.ai.interfaces.ISpawnAPI;
import com.spawn.ai.model.BotMLResponse;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.network.NLPInterceptor;

import constants.AppConstants;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DumpTask implements Runnable {
    public static String SPAWN_API = "https://spawnai.com/";
    private Object object;

    public DumpTask(Object object) {
        this.object = object;
    }

    @Override
    public void run() {
        //
        if (object instanceof SpawnWikiModel) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NLPInterceptor(AppConstants.NLP_USERNAME, AppConstants.NLP_PASSWORD))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SPAWN_API)
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
                    .addInterceptor(new NLPInterceptor(AppConstants.NLP_USERNAME, AppConstants.NLP_PASSWORD))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SPAWN_API)
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


