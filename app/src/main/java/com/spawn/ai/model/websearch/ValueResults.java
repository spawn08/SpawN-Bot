package com.spawn.ai.model.websearch;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ValueResults {

    private String name;
    private String displayUrl;
    private String snippet;
    private String thumbnailUrl;
    private String language;
    private String url;
    private String ampUrl;
    private ArrayList<DeepLinks> deepLinks;
    private int viewType;
}
