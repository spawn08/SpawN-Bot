package com.spawn.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is responsible for chat conversation.
 * This is master class for populating the chats. The ArrayList of this
 * class object is passed to adapter.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatMessageType {

    private String message;
    private int viewType;
    private BotResponse botResponse;
    private String date;
    private String buttonText;
    private String action;
    private boolean speakFinish;
    private boolean actionCompleted;
    private boolean messageAdded;
    private SpawnWikiModel spawnWikiModel;
    private String shortMessage;
    private ChatCardModel chatCardModel;
}
