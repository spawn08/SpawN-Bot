package com.spawn.ai.model.websearch;

import lombok.Data;

@Data
public class WebSearchResults {

    private WebPages webPages;
    private News news;
    private Videos videos;
    private Images images;
}
