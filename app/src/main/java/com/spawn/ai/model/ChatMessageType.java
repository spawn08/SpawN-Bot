package com.spawn.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    This class is responsible for chat conversation.
    This is master class for populating the chats. The ArrayList of this
    class object is passed to adapter.
*/
@NoArgsConstructor
@AllArgsConstructor
@Data
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
    private ChatCardModel chatCardModel;
}
