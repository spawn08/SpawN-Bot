package com.spawn.ai.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spawn.ai.R;

public class SpawnNewsViewHolder extends RecyclerView.ViewHolder {

    public ImageView newsImage;
    public TextView newsDesc;
    public RelativeLayout containerNews;

    public SpawnNewsViewHolder(@NonNull View itemView) {
        super(itemView);
        newsImage = itemView.findViewById(R.id.newsImage);
        newsDesc = itemView.findViewById(R.id.news_desc);
        containerNews = itemView.findViewById(R.id.containerNews);
    }
}
