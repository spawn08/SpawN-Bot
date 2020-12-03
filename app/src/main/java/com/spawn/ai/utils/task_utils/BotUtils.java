package com.spawn.ai.utils.task_utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.reactivex.rxjava3.core.Observable;

import static com.spawn.ai.constants.AppConstants.CLASSES;
import static com.spawn.ai.constants.AppConstants.LANG_EN;
import static com.spawn.ai.constants.AppConstants.SUCCESS;
import static com.spawn.ai.constants.AppConstants.WORDS;

public class BotUtils {

    private static BotUtils botUtils;
    private JSONObject jsonEn, jsonHi;
    private LancasterStemmer stemmer;
    private boolean isEn;
    private static String fileContents;
    private static float THRESHOLD = 0.80f;
    private Interpreter modelInterpreter;

    public static BotUtils getInstance() {
        if (botUtils == null) {
            botUtils = new BotUtils();
        }
        return botUtils;
    }

    public Observable<String> buildInterpreter(Context context, String language) {
        try {
            modelInterpreter = new Interpreter(getLocalModelType(context, language));

            if (jsonEn == null)
                jsonEn = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context, "bot_en/data_en.json")));

            if (jsonHi == null)
                jsonHi = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context, "bot_hi/data_hi.json")));

            if (stemmer == null)
                stemmer = new LancasterStemmer(context);

            fileContents = JsonFileReader.getInstance().getFileContents();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Observable.just(SUCCESS);
    }

    /**
     * Load TFLite model from modelpath
     *
     * @param context   context
     * @param modelPath path where model is present
     * @return MappedByteBuffer object
     */
    private static MappedByteBuffer loadModelFile(Context context, String modelPath)
            throws IOException {
        try (AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    /**
     * Load model based on language
     *
     * @param context  Context for loading assets
     * @param language language of the app
     * @return ByteBuffer for model loaded
     */
    private ByteBuffer getLocalModelType(Context context, String language) throws IOException {
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
            json = new String(buffer, StandardCharsets.UTF_8);
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

            float[][] output = new float[1][classesArray.length()];
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

            modelInterpreter.run(input, output);
            float[] probabilities = output[0];
            int index = getIndexOfLargest(output[0]);
            JSONObject filecontents = new JSONObject(fileContents);
            if (probabilities[index] > THRESHOLD) {
                response = Observable.just(filecontents.getJSONObject(classesArray.getString(index)));
            } else {
                return Observable.just(new JSONObject());
            }

            Log.e("Model Output: ", classesArray.getString(index));
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

