package com.spawn.ai.interfaces;

import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.model.SpawnWikiModel;

public interface IBotObserver {

    void notifyBotResponse(ChatCardModel botResponse);

    void notifyBotError();

    void loading();

    void speakBot(String message);

    void setAction(String action, Object object);

    void setChatMessage(ChatMessageType chatMessage);
}
