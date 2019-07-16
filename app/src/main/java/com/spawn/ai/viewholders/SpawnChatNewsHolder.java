package com.spawn.ai.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spawn.ai.R;

public class SpawnChatNewsHolder extends RecyclerView.ViewHolder {

    public RecyclerView newRecycler;

    public SpawnChatNewsHolder(@NonNull View itemView) {
        super(itemView);

        newRecycler = itemView.findViewById(R.id.newsRecycler);

    }
}
