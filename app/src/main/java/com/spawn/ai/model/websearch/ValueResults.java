package com.spawn.ai.model.websearch;

import java.util.ArrayList;

public class ValueResults {

    private String name;
    private String displayUrl;
    private String snippet;
    private String thumbnailUrl;
    private String language;
    private String url;
    private ArrayList<DeepLinks> deepLinks;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ArrayList<DeepLinks> getDeepLinks() {
        return deepLinks;
    }

    public void setDeepLinks(ArrayList<DeepLinks> deepLinks) {
        this.deepLinks = deepLinks;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
