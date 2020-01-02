package com.spawn.ai.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.spawn.ai.R;

public class SpawnChatNewsHolder extends RecyclerView.ViewHolder {

    public RecyclerView newRecycler;

    public SpawnChatNewsHolder(@NonNull View itemView) {
        super(itemView);

        newRecycler = itemView.findViewById(R.id.newsRecycler);

    }
}
