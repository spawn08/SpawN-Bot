package com.spawn.ai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.spawn.ai.R;
import com.spawn.ai.model.websearch.NewsValue;
import com.spawn.ai.viewholders.websearch_holders.ImageResultHolder;

import java.util.ArrayList;

public class SpawnImageResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<NewsValue> imageResult;

    public SpawnImageResultAdapter(Context context, ArrayList<NewsValue> imageResult) {
        this.imageResult = imageResult;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_result_view, parent, false);
        ImageResultHolder imageResultHolder = new ImageResultHolder(view);
        return imageResultHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ImageResultHolder imageResultHolder = (ImageResultHolder) holder;

        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.thumbnail_not_found)
                        .error(R.drawable.thumbnail_not_found))
                .load(imageResult.get(position).getThumbnailUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(imageResultHolder.imageResult);
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
        return imageResult.size();
    }

}

