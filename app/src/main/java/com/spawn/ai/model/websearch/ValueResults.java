package com.spawn.ai.model.websearch;

public class ValueResults {

    private String name;
    private String displayUrl;
    private String snippet;
    private String thumbnailUrl;
    private String language;

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
}
