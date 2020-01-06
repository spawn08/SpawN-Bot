package com.spawn.ai.viewholders.websearch_holders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spawn.ai.R;

public class ImageResultHolder extends RecyclerView.ViewHolder {

    public ImageView imageResult;

    public ImageResultHolder(@NonNull View itemView) {
        super(itemView);
        imageResult = itemView.findViewById(R.id.imageResult);
    }
}
