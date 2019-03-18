package com.spawn.ai.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spawn.ai.R;
import com.spawn.ai.viewholders.SpawnChatViewHolder;

public class SpawnChatbotAdapter extends RecyclerView.Adapter<SpawnChatViewHolder> {

    private Context context;

    public SpawnChatbotAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public SpawnChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.spawn_chat_recycler, parent, false);
        SpawnChatViewHolder spawnChatViewHolder = new SpawnChatViewHolder(view);
        return spawnChatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SpawnChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
