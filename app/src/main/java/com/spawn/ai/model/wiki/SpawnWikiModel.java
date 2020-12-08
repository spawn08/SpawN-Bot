package com.spawn.ai.model.wiki;

import com.spawn.ai.model.wiki.contenturls.ContentUrls;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by amarthakur on 11/02/19.
 */
@Data
@NoArgsConstructor
public class SpawnWikiModel {
    public String title;
    public String displaytitle;
    public String description;
    public String extract;
    public String lang;
    public String query;
    public Thumbnail thumbnail;
    public String type;
    public ContentUrls content_urls;
}
