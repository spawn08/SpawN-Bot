package com.spawn.ai.utils.task_utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spawn.ai.model.localdata.PreprocessData;

import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

import static com.spawn.ai.constants.AppConstants.LANG_EN;
import static com.spawn.ai.constants.AppConstants.PREPROCESS_EN_FILE;
import static com.spawn.ai.constants.AppConstants.PREPROCESS_HI_FILE;
import static com.spawn.ai.constants.AppConstants.SUCCESS;

@Singleton
public class BotUtils {

    private PreprocessData preprocessDataEn, preprocessDataHi;
    private LancasterStemmer stemmer;
    private boolean isEn;
    private static String fileContents;
    private static float THRESHOLD = 0.80f;
    private Interpreter modelInterpreter;

    private final AppUtils appUtils;

    @Inject
    public BotUtils(AppUtils appUtils){
        this.appUtils = appUtils;
    }

    public Observable<String> buildInterpreter(Context context, String language) {
        try {
            modelInterpreter = new Interpreter(getLocalModelType(context, language));

            if (preprocessDataEn == null) {
                Gson gson = new Gson();
                Type type = new TypeToken<PreprocessData>() {
                }.getType();
                preprocessDataEn = gson.fromJson(appUtils.loadJSONFromAsset(context, PREPROCESS_EN_FILE), type);
            }

            if (preprocessDataHi == null) {
                Gson gson = new Gson();
                Type type = new TypeToken<PreprocessData>() {
                }.getType();
                preprocessDataHi = gson.fromJson(appUtils.loadJSONFromAsset(context, PREPROCESS_HI_FILE), type);
            }

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

    /**
     * Perform inference on user defined sentence based on language model
     *
     * @param sentence User defined query
     * @param language language of the query
     * @return result in {@link JSONObject} format
     */
    public Observable<JSONObject> classify(String sentence, String language) {
        Observable<JSONObject> response;
        try {
            PreprocessData preprocessData = language.equalsIgnoreCase(LANG_EN) ? preprocessDataEn : preprocessDataHi;
            ArrayList<String> wordsArray = preprocessData.getWords();
            ArrayList<String> classesArray = preprocessData.getClasses();

            float[][] output = new float[1][classesArray.size()];
            float[][] input = new float[1][wordsArray.size()];
            input = appUtils.getInputFeatures(sentence, input, wordsArray, stemmer, isEn);

            modelInterpreter.run(input, output);
            float[] probabilities = output[0];
            int index = appUtils.getIndexOfLargest(output[0]);
            JSONObject filecontents = new JSONObject(fileContents);
            if (probabilities[index] > THRESHOLD) {
                response = Observable.just(filecontents.getJSONObject(classesArray.get(index)));
            } else {
                return Observable.just(new JSONObject());
            }

            Log.e("Model Output: ", classesArray.get(index));
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.just(new JSONObject());
        }
        return response;
    }
}

