package com.spawn.ai.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class JsonFileReader {

    private static JsonFileReader jsonFileReader;

    public static JsonFileReader getInstance() {
        if (jsonFileReader == null) {
            jsonFileReader = new JsonFileReader();
        }
        return jsonFileReader;
    }

    public String readFile(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            json = new String(bytes, "utf-8");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public JSONObject convertToJson(String contents) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(contents);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    public JSONObject getJsonFromKey(JSONObject jsonObject, String key) {
        try {
            JSONObject data = jsonObject.getJSONObject(key);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
