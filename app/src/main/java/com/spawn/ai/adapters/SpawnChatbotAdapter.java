package com.spawn.ai.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spawn.ai.R;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.viewholders.SpawnChatBotViewHolder;
import com.spawn.ai.viewholders.SpawnChatViewHolder;

import java.util.ArrayList;

import constants.ChatViewTypes;

public class SpawnChatbotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ChatMessageType> chatMessageType;

    public SpawnChatbotAdapter(Context context, ArrayList<ChatMessageType> chatMessageType) {
        this.context = context;
        this.chatMessageType = chatMessageType;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(context).inflate(R.layout.spawn_chat_user, parent, false);
                SpawnChatViewHolder spawnChatViewHolder = new SpawnChatViewHolder(view);
                return spawnChatViewHolder;

            case 1:
                View viewBot = LayoutInflater.from(context).inflate(R.layout.spawn_chat_bot, parent, false);
                SpawnChatBotViewHolder spawnChatBotViewHolder = new SpawnChatBotViewHolder(viewBot);
                return spawnChatBotViewHolder;

        }

        View view = LayoutInflater.from(context).inflate(R.layout.spawn_chat_user, parent, false);
        SpawnChatViewHolder spawnChatViewHolder = new SpawnChatViewHolder(view);
        return spawnChatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (chatMessageType.get(position).getViewType()) {
            case ChatViewTypes.CHAT_VIEW_USER:


                break;
            case ChatViewTypes.CHAT_VIEW_BOT:

                break;
        }

    }

    @Override
    public int getItemCount() {
        return chatMessageType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessageType.get(position).getViewType();
    }
}
