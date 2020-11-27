package com.spawn.ai.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.spawn.ai.R;
import com.spawn.ai.custom.DotProgressBar;

public class SpawnChatLoadingViewHolder extends RecyclerView.ViewHolder {

    public DotProgressBar loading;

    public SpawnChatLoadingViewHolder(View itemView) {
        super(itemView);

        loading = itemView.findViewById(R.id.bot_loading);

    }
}
