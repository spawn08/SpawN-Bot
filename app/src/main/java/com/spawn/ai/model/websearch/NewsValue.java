package com.spawn.ai.model.websearch;

import lombok.Data;

@Data
public class NewsValue {

    private String name;
    private String url;
    private Image image;
    private String description;
    private String category;
    private String ampUrl;
    private String thumbnailUrl;
}
