package com.spawn.ai.utils.task_utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import androidx.lifecycle.MutableLiveData;

public class BotUtils {

    private static BotUtils botUtils;
    private JSONObject jsonEn, jsonHi;
    private LancasterStemmer stemmer;
    private boolean isEn;
    private static String fileContents;
    private static float THRESHOLD = 0.80f;

    public static BotUtils getInstance() {
        if (botUtils == null) {
            botUtils = new BotUtils();
        }
        return botUtils;
    }

    public Observable<String> buildInterpreter(Context context, String language) {
        try {
            FirebaseCustomLocalModel customLocalModel = getLocalModelType(language);
            options = new FirebaseModelInterpreterOptions.Builder(customLocalModel).build();

            this.firebaseModelInterpreter = FirebaseModelInterpreter.getInstance(options);

            if (jsonEn == null)
                jsonEn = new JSONObject(loadJSONFromAsset(context, "bot_en/data_en.json"));

            if (jsonHi == null)
                jsonHi = new JSONObject(loadJSONFromAsset(context, "bot_hi/data_hi.json"));

            if (stemmer == null)
                stemmer = new LancasterStemmer(context);

            fileContents = JsonFileReader.getInstance().getFileContents();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Observable.just(SUCCESS);
    }

    private FirebaseCustomLocalModel getLocalModelType(String language) {
        if (language.equalsIgnoreCase("en")) {
            isEn = true;
            THRESHOLD = 0.80f;
            return loadModelFile(context, "bot_en/spawn_en.tflite");
        } else {
            isEn = false;
            THRESHOLD = 0.70f;
            return loadModelFile(context, "bot_hi/spawn_hi.tflite");
        }
    }

    private String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public Observable<JSONObject> classify(String sentence, String language) {
        Observable<JSONObject> response;
        try {
            JSONObject jsonObject = language.equalsIgnoreCase(LANG_EN) ? jsonEn : jsonHi;
            JSONArray wordsArray = jsonObject.getJSONArray(WORDS);
            JSONArray classesArray = jsonObject.getJSONArray(CLASSES);

            String[] sent = sentence.split(" ");
            float[][] input = new float[1][wordsArray.length()];
            for (String s : sent) {
                for (int i = 0; i < wordsArray.length(); i++) {
                    String wordIndex = wordsArray.getString(i);
                    if (stemmer.stem(s).equalsIgnoreCase(wordIndex) && isEn)
                        input[0][i] = 1.0f;
                    else if (s.equalsIgnoreCase(wordIndex))
                        input[0][i] = 1.0f;
                }
            }
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                    .add(input)
                    .build();
            this.firebaseModelInterpreter.run(inputs, inputOutputOptions)
                    .addOnSuccessListener(
                            result -> {
                                try {
                                    float[][] output = result.getOutput(0);
                                    float[] probabilities = output[0];
                                    int index = getIndexOfLargest(probabilities);
                                    JSONObject filecontents = new JSONObject(fileContents);
                                    if (probabilities[index] > THRESHOLD) {
                                        if (filecontents.getJSONObject(classesArray.getString(index)) != null) {
                                            JSONObject response = filecontents.getJSONObject(classesArray.getString(index));
                                            data.setValue(response);
                                        } else data.setValue(null);
                                    } else {
//                                        JSONObject response = filecontents.getJSONObject("default");
//                                        data.postValue(response);
                                        data.setValue(null);
                                    }
                                    Log.e("Bot Response", String.format("%s: %1.4f", classesArray.getString(index), probabilities[index]));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    data.setValue(null);
                                }
                            })
                    .addOnFailureListener(
                            e -> {
                                Log.e("Failed", "NO RESULT");
                                data.setValue(null);
                            });
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.just(new JSONObject());
        }
        return response;
    }

    public int getIndexOfLargest(float[] array) {
        if (array == null || array.length == 0) return -1;

        int largest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[largest]) largest = i;
        }
        return largest;
    }
}

