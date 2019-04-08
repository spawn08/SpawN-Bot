package com.spawn.ai.interfaces;

import com.spawn.ai.model.ChatCardModel;

public interface IBotObserver {

    public void notifyBotResponse(ChatCardModel botResponse);

    public void notifyBotError();

    public void loading();

    public void speakBot(String message);

    public void setAction(String action);
}
