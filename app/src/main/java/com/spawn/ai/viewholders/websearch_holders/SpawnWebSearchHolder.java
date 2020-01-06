package com.spawn.ai.viewholders.websearch_holders;

import android.view.View;
import android.widget.TextView;

import com.spawn.ai.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpawnWebSearchHolder extends RecyclerView.ViewHolder {

    public RecyclerView webRecycler, newsList, imageList, videoList;
    public TextView imageText, newsText, videoText;

    public SpawnWebSearchHolder(@NonNull View itemView) {
        super(itemView);

        webRecycler = itemView.findViewById(R.id.webRecycler);
        newsList = itemView.findViewById(R.id.newsList);
        imageList = itemView.findViewById(R.id.imageList);
        videoList = itemView.findViewById(R.id.videoList);
        imageText = itemView.findViewById(R.id.imageText);
        newsText = itemView.findViewById(R.id.newsText);
        videoText = itemView.findViewById(R.id.videoText);
    }
}
