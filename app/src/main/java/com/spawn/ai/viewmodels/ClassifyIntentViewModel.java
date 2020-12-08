package com.spawn.ai.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.repository.ClassifyIntentRespository;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

public class ClassifyIntentViewModel extends ViewModel {

    private final ClassifyIntentRespository classifyIntentRespository;

    @Inject
    public ClassifyIntentViewModel(ClassifyIntentRespository classifyIntentRespository) {
        this.classifyIntentRespository = classifyIntentRespository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        classifyIntentRespository.getCompositeDisposable().dispose();
    }

    /**
     * Perform classification task on sentence
     *
     * @param sentence user defined query
     * @param language language of the user query
     * @return LiveData object to observe on
     */
    public LiveData<JSONObject> classify(String sentence, String language) {
        return classifyIntentRespository.classify(sentence, language);
    }

    /**
     * Perform websearch on user query
     *
     * @param q        user defined query
     * @param language language of the query
     * @param type     e.g. WEB, NEWS, IMAGES etc
     * @return ChatCardModel
     * @throws UnsupportedEncodingException
     */
    public LiveData<ChatCardModel> getWebSearch(String q, String language, String type) throws UnsupportedEncodingException {
        return classifyIntentRespository.getWebSearch(q, language, type);
    }

    /**
     * Get API response from WikiPedia API
     *
     * @param entity   entity like person, place, monuments etc to search on wikipedia
     * @param language language of the data content e.g. en, hi
     * @return ChatCardModel
     */
    public LiveData<ChatCardModel> getWikiResponse(String entity, String language) {
        return classifyIntentRespository.getWikiResponse(entity, language);
    }
}
