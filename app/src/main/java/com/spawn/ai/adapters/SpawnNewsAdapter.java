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
import com.spawn.ai.viewholders.SpawnNewsViewHolder;

import org.json.JSONArray;

public class SpawnNewsAdapter extends RecyclerView.Adapter<SpawnNewsViewHolder> {

    private Context context;
    private JSONArray jsonArray;

    public SpawnNewsAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
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
                    .load(jsonArray.getJSONObject(i).getString("image"))
                    .apply(RequestOptions.circleCropTransform())
                    .into(spawnNewsViewHolder.newsImage);
            spawnNewsViewHolder.newsDesc.setText(jsonArray.getJSONObject(i).getString("title"));

            spawnNewsViewHolder.containerNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, SpawnWebActivity.class);
                        intent.putExtra("url", jsonArray.getJSONObject(i).getString("link"));
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
        return jsonArray.length();
    }
}
