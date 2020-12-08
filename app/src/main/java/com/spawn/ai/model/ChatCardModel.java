package com.spawn.ai.model;

import com.spawn.ai.model.websearch.News;
import com.spawn.ai.model.websearch.WebSearchResults;
import com.spawn.ai.model.wiki.SpawnWikiModel;

public class ChatCardModel {

    private String button_text;
    private String message;
    private int type;
    private String action;
    private SpawnWikiModel spawnWikiModel;
    private WebSearchResults webSearchResults;
    private News news;
    private String lang;

    public ChatCardModel() {

    }

    public ChatCardModel(String text, String message, int type, String action) {
        this.button_text = text;
        this.message = message;
        this.type = type;
        this.action = action;
    }

    public ChatCardModel(SpawnWikiModel spawnWikiModel, int type) {
        this.spawnWikiModel = spawnWikiModel;
        this.type = type;
    }

    public ChatCardModel(WebSearchResults webSearchResults, int type) {
        this.webSearchResults = webSearchResults;
        this.type = type;
    }

    public ChatCardModel(News news, int type) {
        this.news = news;
        this.type = type;
    }

    public String getButton_text() {
        return this.button_text;
    }

    public String getMessage() {
        return this.message;
    }

    public int getType() {
        return this.type;
    }

    public String getAction() {
        return this.action;
    }

    public SpawnWikiModel getSpawnWikiModel() {
        return spawnWikiModel;
    }

    public WebSearchResults getWebSearchResults() {
        return webSearchResults;
    }

    public void setWebSearchResults(WebSearchResults webSearchResults) {
        this.webSearchResults = webSearchResults;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setButton_text(String button_text) {
        this.button_text = button_text;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
