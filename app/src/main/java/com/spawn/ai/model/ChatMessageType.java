package com.spawn.ai.model;

public class ChatMessageType {

    private String message;

    //0=User chat
    //1= Bot Chat
    private int viewType;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
