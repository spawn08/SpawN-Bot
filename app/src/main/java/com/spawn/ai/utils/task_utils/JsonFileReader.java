package com.spawn.ai.utils.task_utils;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonElement;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.model.ChatCardModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class JsonFileReader {

    private static JsonFileReader jsonFileReader;
    private String fileContents;
    private String fileName = AppUtils.getInstance().getDataFile();
    private ChatCardModel cardModel;
    private ArrayList<String> questions = new ArrayList<>();
    private HashMap<String, ArrayList<String>> questionsMap = new HashMap<>();

    private JsonFileReader() {

    }

    public static JsonFileReader getInstance() {
        if (jsonFileReader == null) {
            jsonFileReader = new JsonFileReader();
        }
        return jsonFileReader;
    }

    public void fileName(String fileName) {
        this.fileName = fileName;
    }

    public void readFile(Context context, JsonElement file) {
        try {
            if (file != null)
                fileContents = file.toString();
            else {
                fileContents = null;
                Log.d(JsonFileReader.class.getSimpleName(), "File from server " + fileContents);
                loadLocalFile(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadLocalFile(context);
            Crashlytics.logException(e);
        }
    }

    private void loadLocalFile(Context context) {
        if (fileContents == null || fileContents.isEmpty()) {
            String json;
            try {
                InputStream is = context.getAssets().open(fileName);
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                is.close();
                json = new String(bytes, "utf-8");
                this.fileContents = json;
                Log.d(JsonFileReader.class.getSimpleName(), "File read from asset");
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public JSONObject convertToJson(String contents) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(contents);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        }
        return jsonObject;
    }

    public ChatCardModel getJsonFromKey(String key, int i, String lang) {
        String message;
        ChatCardModel chatCardModel = new ChatCardModel("", getDefaultAnswer(lang), 1, "");
        try {
            switch (i) {
                case ChatViewTypes.CHAT_VIEW_BOT:
                    if (fileContents != null) {
                        JSONObject data = new JSONObject(fileContents);
                        JSONObject body = (data.has(key)) ? data.getJSONObject(key) : data.getJSONObject("default");
                        JSONArray jsonArray = body.getJSONArray("message_" + lang);
                        int index = new Random().nextInt(jsonArray.length());
                        message = jsonArray.get(index).toString();
                        chatCardModel = new ChatCardModel(body.getString("button_text_" + lang),
                                message,
                                body.getInt("type"), body.getString("action"));
                        setCardModel(chatCardModel);
                        return chatCardModel;
                    }

                case ChatViewTypes.CHAT_VIEW_CARD:
                    if (fileContents != null) {
                        JSONObject data = new JSONObject(fileContents);
                        JSONObject body = (data.has(key)) ? data.getJSONObject(key) : data.getJSONObject("default");
                        JSONArray jsonArray = body.getJSONArray("message_" + lang);
                        int index = new Random().nextInt(jsonArray.length());
                        message = jsonArray.get(index).toString();
                        chatCardModel = new ChatCardModel(body.getString("button_text_" + lang),
                                message,
                                body.getInt("type"), body.getString("action"));
                        setCardModel(chatCardModel);
                        return chatCardModel;
                    }

                case ChatViewTypes.CHAT_VIEW_DEFAULT:
                    if (fileContents != null) {
                        JSONObject data = new JSONObject(fileContents);
                        JSONObject body = (data.has(key)) ? data.getJSONObject(key) : data.getJSONObject("default");
                        JSONArray jsonArray = body.getJSONArray("message_" + lang);
                        int index = new Random().nextInt(jsonArray.length());
                        message = jsonArray.get(index).toString();
                        chatCardModel = new ChatCardModel(body.getString("button_text_" + lang),
                                message,
                                body.getInt("type"), body.getString("action"));
                        setCardModel(chatCardModel);
                        return chatCardModel;
                    }
                    return chatCardModel;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return chatCardModel;
        }
        return chatCardModel;
    }

    private void setCardModel(ChatCardModel cardModel) {
        this.cardModel = cardModel;
    }

    public ChatCardModel getCardModel() {
        return this.cardModel;
    }

    private String getDefaultAnswer(String lang) {
        String message = "";
        if (fileContents != null) {
            try {
                JSONObject data = new JSONObject(fileContents);
                if (data.has("default")) {
                    JSONObject body = data.getJSONObject("default");
                    JSONArray jsonArray = body.getJSONArray("message_" + lang);
                    int index = new Random().nextInt(jsonArray.length());
                    message = jsonArray.get(index).toString();
                } else {
                    message = "Sorry, I could not understand what you just said";
                }
                return message;
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
                message = "Sorry, I could not understand what you just said";
            }
        }
        return message;
    }

    public void setQuestions(String lang) {
        if (fileContents != null) {
            questions = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(fileContents);
                JSONArray jsonArray = jsonObject.getJSONArray("questions_" + lang);
                for (int i = 0; i < jsonArray.length(); i++) {
                    questions.add(jsonArray.getString(i));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
    }

    public String getValueFromJson(String key) {
        String value = "";
        if (fileContents != null) {
            try {
                JSONObject jsonObject = new JSONObject(fileContents);
                if (jsonObject.has(key))
                    value = jsonObject.getString(key);
                return value;

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
                return value;
            }
        }
        return value;
    }

    public ArrayList<String> getQuestions(String lang) {
        if (questionsMap.get(lang) == null) {
            setQuestions(lang);
            questionsMap.put(lang, questions);
        }

        /*if (questions != null)
            return questions;
        else
            setQuestions(lang);*/
        return questionsMap.get(lang);
    }

//    public String getFileContents() {
//        return this.fileContents;
//    }

}
