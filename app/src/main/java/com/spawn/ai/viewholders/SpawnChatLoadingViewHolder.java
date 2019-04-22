package com.spawn.ai.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.spawn.ai.R;
import com.spawn.ai.utils.DotProgressBar;

public class SpawnChatLoadingViewHolder extends RecyclerView.ViewHolder {

    public DotProgressBar loading;

    public SpawnChatLoadingViewHolder(View itemView) {
        super(itemView);

        loading = (DotProgressBar) itemView.findViewById(R.id.bot_loading);

    }
}
