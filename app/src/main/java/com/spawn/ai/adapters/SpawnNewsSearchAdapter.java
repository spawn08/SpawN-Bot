package com.spawn.ai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spawn.ai.R;
import com.spawn.ai.activities.SpawnWebActivity;
import com.spawn.ai.model.websearch.NewsValue;
import com.spawn.ai.utils.task_utils.AppUtils;
import com.spawn.ai.viewholders.websearch_holders.WebResultHolder;

import java.util.ArrayList;

public class SpawnNewsSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<NewsValue> newsResult;

    public SpawnNewsSearchAdapter(Context context, ArrayList<NewsValue> newsResult) {
        this.newsResult = newsResult;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.web_result_views, parent, false);
        WebResultHolder webResultHolder = new WebResultHolder(view);
        return webResultHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        WebResultHolder webResultHolder = (WebResultHolder) holder;

        if (newsResult.get(position).getName() != null)
            webResultHolder.webTile.setText(newsResult.get(position).getName());
        if (newsResult.get(position).getUrl() != null)
            webResultHolder.webDisplayUrl.setText(newsResult.get(position).getUrl());
        if (newsResult.get(position).getDescription() != null)
            webResultHolder.webDescription.setText(AppUtils.getInstance().getInfoFromExtract(newsResult.get(position).getDescription(), "speak"));
        webResultHolder.webTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, SpawnWebActivity.class);
                    if (newsResult.get(position).getAmpUrl() != null)
                        intent.putExtra("url", newsResult.get(position).getAmpUrl());
                    else intent.putExtra("url", newsResult.get(position).getUrl());
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsResult.size();
    }

}

