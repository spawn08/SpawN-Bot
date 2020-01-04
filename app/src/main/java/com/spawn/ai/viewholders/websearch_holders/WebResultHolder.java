package com.spawn.ai.viewholders.websearch_holders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spawn.ai.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class WebResultHolder extends RecyclerView.ViewHolder {

    public CardView webCardView;
    public ImageView webImage;
    public TextView webDescription, webTile;
    public Button webButton;
    //  public RelativeLayout imageRel;

    public WebResultHolder(@NonNull View itemView) {
        super(itemView);

        webCardView = itemView.findViewById(R.id.cardViewWeb);
        webImage = itemView.findViewById(R.id.web_image);
        webDescription = itemView.findViewById(R.id.web_description);
        webTile = itemView.findViewById(R.id.web_title);
        webButton = itemView.findViewById(R.id.web_button);
        //  imageRel = itemView.findViewById(R.id.imageRel);
    }
}
