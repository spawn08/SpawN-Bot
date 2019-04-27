package com.spawn.ai.interfaces;

import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.model.SpawnWikiModel;

public interface IBotObserver {

    public void notifyBotResponse(ChatCardModel botResponse);

    public void notifyBotError();

    public void loading();

    public void speakBot(String message);

    public void setAction(String action, SpawnWikiModel spawnWikiModel);

    public void setChatMessage(ChatMessageType chatMessage);
}
