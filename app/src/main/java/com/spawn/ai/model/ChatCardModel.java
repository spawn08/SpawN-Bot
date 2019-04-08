package com.spawn.ai.model;

public class ChatCardModel {

    String button_text;
    String message;
    int type;
    String action;

    public ChatCardModel(String text, String message, int type, String action) {
        this.button_text = text;
        this.message = message;
        this.type = type;
        this.action = action;
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
}
