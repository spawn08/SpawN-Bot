package com.spawn.ai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.spawn.ai.R;
import com.spawn.ai.activities.SpawnWebActivity;
import com.spawn.ai.model.websearch.VideoValueResult;
import com.spawn.ai.viewholders.websearch_holders.VideoResultHolder;

import java.util.ArrayList;

public class SpawnVideoResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<VideoValueResult> videoResult;

    public SpawnVideoResultAdapter(Context context, ArrayList<VideoValueResult> videoResult) {
        this.videoResult = videoResult;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_result_view, parent, false);
        return new VideoResultHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        VideoResultHolder videoResultHolder = (VideoResultHolder) holder;
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.thumbnail_not_found)
                        .error(R.drawable.thumbnail_not_found))
                .load(videoResult.get(position).getThumbnailUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(videoResultHolder.videoView);
        //videoResultHolder.videoView.setVideoPath(videoResult.get(position).getHostPageDisplayUrl());
        //videoResultHolder.videoView.seekTo(1);
        videoResultHolder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SpawnWebActivity.class);
                intent.putExtra("url", videoResult.get(position).getHostPageUrl());
                context.startActivity(intent);
            }
        });
//        imageResultHolder.imageResult.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Intent intent = new Intent(context, SpawnWebActivity.class);
//                    if (imageResult.get(position).getAmpUrl() != null)
//                        intent.putExtra("url", imageResult.get(position).getAmpUrl());
//                    else intent.putExtra("url", imageResult.get(position).getUrl());
//                    context.startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return videoResult.size();
    }

}

