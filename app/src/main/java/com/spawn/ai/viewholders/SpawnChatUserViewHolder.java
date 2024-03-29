package com.spawn.ai.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.spawn.ai.R;

public class SpawnChatUserViewHolder extends RecyclerView.ViewHolder {

    public TextView user_message, user_time;

    public SpawnChatUserViewHolder(View itemView) {
        super(itemView);

        user_message = itemView.findViewById(R.id.user_message);
        user_time = itemView.findViewById(R.id.user_time);

    }
}
