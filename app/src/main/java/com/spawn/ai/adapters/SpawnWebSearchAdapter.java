package com.spawn.ai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.spawn.ai.R;
import com.spawn.ai.activities.SpawnWebActivity;
import com.spawn.ai.model.websearch.ValueResults;
import com.spawn.ai.utils.task_utils.AppUtils;
import com.spawn.ai.utils.task_utils.SharedPreferenceUtility;
import com.spawn.ai.viewholders.websearch_holders.WebResultHolder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpawnWebSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ValueResults> valueResults;

    public SpawnWebSearchAdapter(Context context, ArrayList<ValueResults> valueResults) {
        this.valueResults = valueResults;
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
        if (valueResults.get(position).getName().contains("Wikipedia")) {
            webResultHolder.webButton.setText(valueResults.get(position).getName());
            if (valueResults.get(position).getThumbnailUrl() != null)
                Glide.with(context)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .placeholder(R.drawable.thumbnail_not_found)
                                .error(R.drawable.thumbnail_not_found))
                        .load(valueResults.get(position).getThumbnailUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(webResultHolder.webImage);
            webResultHolder.webTile.setText(valueResults.get(position).getName());
            webResultHolder.webDescription.setText(AppUtils.getInstance().getInfoFromExtract(valueResults.get(position).getSnippet(), "speak"));
            webResultHolder.webButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, SpawnWebActivity.class);
                        intent.putExtra("url", valueResults.get(position).getUrl());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            webResultHolder.webCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, SpawnWebActivity.class);
                        intent.putExtra("url", valueResults.get(position).getUrl());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            webResultHolder.webButton.setText(AppUtils.getStringRes(R.string.info, context, SharedPreferenceUtility.getInstance(context).getStringPreference("lang")));
            if (valueResults.get(position).getThumbnailUrl() != null)
                Glide.with(context)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .placeholder(R.drawable.thumbnail_not_found)
                                .error(R.drawable.thumbnail_not_found))
                        .load(valueResults.get(position).getThumbnailUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(webResultHolder.webImage);
            webResultHolder.webTile.setText(valueResults.get(position).getName());
            webResultHolder.webDescription.setText(AppUtils.getInstance().getInfoFromExtract(valueResults.get(position).getSnippet(), "speak"));
            webResultHolder.webButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, SpawnWebActivity.class);
                        intent.putExtra("url", valueResults.get(position).getUrl());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            webResultHolder.webCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, SpawnWebActivity.class);
                        intent.putExtra("url", valueResults.get(position).getUrl());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return valueResults.size();
    }
}
