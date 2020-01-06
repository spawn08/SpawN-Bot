package com.spawn.ai.viewholders.websearch_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spawn.ai.R;

public class VideoResultHolder extends RecyclerView.ViewHolder {

    public ImageView videoView;

    public VideoResultHolder(@NonNull View itemView) {
        super(itemView);
        videoView = itemView.findViewById(R.id.videoView);

    }
}
