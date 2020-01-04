package com.spawn.ai.viewholders.websearch_holders;

import android.view.View;

import com.spawn.ai.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpawnWebSearchHolder extends RecyclerView.ViewHolder {

    public RecyclerView webRecycler;

    public SpawnWebSearchHolder(@NonNull View itemView) {
        super(itemView);

        webRecycler = itemView.findViewById(R.id.webRecycler);
    }
}
