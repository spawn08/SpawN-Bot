package com.spawn.ai.adapters;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.spawn.ai.R;
import com.spawn.ai.activities.SpawnWebActivity;
import com.spawn.ai.model.websearch.NewsValue;
import com.spawn.ai.viewholders.SpawnNewsViewHolder;

import org.json.JSONArray;

import java.util.ArrayList;

public class SpawnNewsAdapter extends RecyclerView.Adapter<SpawnNewsViewHolder> {

    private Context context;
    //  private JSONArray jsonArray;
    private ArrayList<NewsValue> newsValues;

    public SpawnNewsAdapter(Context context, /*JSONArray jsonArray,*/ ArrayList<NewsValue> newsValues) {
        this.context = context;
        // this.jsonArray = jsonArray;
        this.newsValues = newsValues;
    }

    @NonNull
    @Override
    public SpawnNewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.spawn_chat_news, viewGroup, false);
        SpawnNewsViewHolder spawnNewsViewHolder = new SpawnNewsViewHolder(view);
        return spawnNewsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SpawnNewsViewHolder spawnNewsViewHolder, final int i) {
        try {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.thumbnail_not_found).error(R.drawable.thumbnail_not_found))
                    .load(newsValues.get(i).getImage().getThumbnail().getContentUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(spawnNewsViewHolder.newsImage);
            spawnNewsViewHolder.newsDesc.setText(newsValues.get(i).getDescription());

            spawnNewsViewHolder.containerNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, SpawnWebActivity.class);
                        intent.putExtra("url", newsValues.get(i).getUrl());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return newsValues.size();
    }
}
