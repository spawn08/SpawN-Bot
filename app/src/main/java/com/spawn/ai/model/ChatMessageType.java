package com.spawn.ai.model;

/*
    This class is responsible for chat conversation.
    This is master class for populating the chats. The ArrayList of this
    class object is passed to adapter.
*/
public class ChatMessageType {

    private String message;
    private int viewType;
    private BotResponse botResponse;
    private String date;
    private String buttonText;
    private String action;
    private boolean speakFinish = false;
    private boolean actionCompleted = false;
    private boolean messageAdded = false;
    private SpawnWikiModel spawnWikiModel;
    private String shortMessage;

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

    public BotResponse getBotResponse() {
        return botResponse;
    }

    public void setBotResponse(BotResponse botResponse) {
        this.botResponse = botResponse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public SpawnWikiModel getSpawnWikiModel() {
        return spawnWikiModel;
    }

    public void setSpawnWikiModel(SpawnWikiModel spawnWikiModel) {
        this.spawnWikiModel = spawnWikiModel;
    }

    public boolean isSpeakFinish() {
        return speakFinish;
    }

    public void setSpeakFinish(boolean speakFinish) {
        this.speakFinish = speakFinish;
    }

    public boolean isActionCompleted() {
        return actionCompleted;
    }

    public void setActionCompleted(boolean actionCompleted) {
        this.actionCompleted = actionCompleted;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public boolean isMessageAdded() {
        return messageAdded;
    }

    public void setMessageAdded(boolean messageAdded) {
        this.messageAdded = messageAdded;
    }
}
