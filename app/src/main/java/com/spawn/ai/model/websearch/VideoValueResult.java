package com.spawn.ai.model.websearch;

import lombok.Data;

@Data
public class VideoValueResult {

    private String name;
    private String hostPageUrl;
    private Image image;
    private String description;
    private String thumbnailUrl;
    private String hostPageDisplayUrl;
}
