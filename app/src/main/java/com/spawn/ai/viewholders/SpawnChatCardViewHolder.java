package com.spawn.ai.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spawn.ai.R;

public class SpawnChatCardViewHolder extends RecyclerView.ViewHolder {

    public Button card_button;
    public TextView spawn_card_text;

    public SpawnChatCardViewHolder(View itemView) {
        super(itemView);

        card_button = itemView.findViewById(R.id.card_button);
        spawn_card_text = itemView.findViewById(R.id.spawn_card_text);

    }
}
