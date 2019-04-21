package com.spawn.ai.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spawn.ai.R;


public class SpawnWikiViewHolder extends RecyclerView.ViewHolder {

    public TextView wikiParagraph, wikiTitle;
    public ImageView wikiImage;
    public Button wikiButton;

    public SpawnWikiViewHolder(View itemView) {
        super(itemView);
        wikiParagraph = itemView.findViewById(R.id.wiki_paragraph);
        wikiImage = itemView.findViewById(R.id.wiki_image);
        wikiTitle = itemView.findViewById(R.id.wiki_title);
        wikiButton = itemView.findViewById(R.id.wiki_button);
    }
}
