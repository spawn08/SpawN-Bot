package com.spawn.ai.model;

/**
 * Created by amarthakur on 11/02/19.
 */

public class Thumbnail {

    String source;
    String width;
    String height;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Source: " + source + " Height: " + height + " Width: " + width;
    }
}
