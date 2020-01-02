package com.spawn.ai.viewholders;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spawn.ai.R;


public class SpawnWikiViewHolder extends RecyclerView.ViewHolder {

    public TextView wikiParagraph, wikiTitle;
    public ImageView wikiImage;
    public Button wikiButton;
    public CardView cardView;

    public SpawnWikiViewHolder(View itemView) {
        super(itemView);
        wikiParagraph = itemView.findViewById(R.id.wiki_paragraph);
        wikiImage = itemView.findViewById(R.id.wiki_image);
        wikiTitle = itemView.findViewById(R.id.wiki_title);
        wikiButton = itemView.findViewById(R.id.wiki_button);
        cardView = itemView.findViewById(R.id.cardview_id);
    }
}
