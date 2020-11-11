package com.spawn.ai.interfaces;

import com.spawn.ai.model.ChatMessageType;

public interface IBotObserver {
    void notifyBotError();

    void loading();

    void speakBot(String message);

    void setAction(String action, Object object);

    void setChatMessage(ChatMessageType chatMessage);
}
