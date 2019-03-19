package com.spawn.ai.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.Key;
import java.util.Random;

public class JsonFileReader {

    private static JsonFileReader jsonFileReader;
    private String fileContents;

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
            this.fileContents = json;

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

    public String getJsonFromKey(String key) {
        String message = "";
        try {
            if (fileContents != null) {
                JSONObject data = new JSONObject(fileContents);
                JSONObject body = data.getJSONObject(key);
                JSONArray jsonArray = body.getJSONArray("message");
                int index = new Random().nextInt(jsonArray.length());
                message = jsonArray.get(index).toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return message;
    }

    public String getFileContents() {
        return this.fileContents;
    }

}
