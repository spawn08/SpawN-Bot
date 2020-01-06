package com.spawn.ai.model.websearch;

public class WebSearchResults {

    private WebPages webPages;
    private News news;
    private Videos videos;
    private Images images;

    public WebPages getWebPages() {
        return webPages;
    }

    public void setWebPages(WebPages webPages) {
        this.webPages = webPages;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public Videos getVideos() {
        return videos;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }

    public Images getImage() {
        return images;
    }

    public void setImage(Images image) {
        this.images = image;
    }
}
