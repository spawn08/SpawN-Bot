package com.spawn.ai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.spawn.ai.R;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.interfaces.IBotObserver;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.model.SpawnWikiModel;
import com.spawn.ai.model.websearch.News;
import com.spawn.ai.model.websearch.NewsValue;
import com.spawn.ai.model.websearch.ValueResults;
import com.spawn.ai.model.websearch.WebSearchResults;
import com.spawn.ai.utils.task_utils.AppUtils;
import com.spawn.ai.utils.task_utils.SharedPreferenceUtility;
import com.spawn.ai.viewholders.SpawnChatBotViewHolder;
import com.spawn.ai.viewholders.SpawnChatCardViewHolder;
import com.spawn.ai.viewholders.SpawnChatLoadingViewHolder;
import com.spawn.ai.viewholders.SpawnChatNewsHolder;
import com.spawn.ai.viewholders.SpawnChatUserViewHolder;
import com.spawn.ai.viewholders.SpawnWikiViewHolder;
import com.spawn.ai.viewholders.websearch_holders.SpawnWebSearchHolder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpawnChatbotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ChatMessageType> chatMessageType = new ArrayList<>();
    IBotObserver iBotObserver;

    public SpawnChatbotAdapter(Context context, ArrayList<ChatMessageType> chatMessageType) {
        this.context = context;
        this.chatMessageType = chatMessageType;
        iBotObserver = (IBotObserver) context;
    }

    public void setAdapter(ArrayList<ChatMessageType> chatMessageType) {
        this.chatMessageType = chatMessageType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ChatViewTypes.CHAT_VIEW_USER:
                View view = LayoutInflater.from(context).inflate(R.layout.spawn_chat_user, parent, false);
                SpawnChatUserViewHolder spawnChatViewHolder = new SpawnChatUserViewHolder(view);
                return spawnChatViewHolder;

            case ChatViewTypes.CHAT_VIEW_BOT:
                View viewBot = LayoutInflater.from(context).inflate(R.layout.spawn_chat_bot, parent, false);
                SpawnChatBotViewHolder spawnChatBotViewHolder = new SpawnChatBotViewHolder(viewBot);
                return spawnChatBotViewHolder;

            case ChatViewTypes.CHAT_VIEW_LOADING:
                View viewLoading = LayoutInflater.from(context).inflate(R.layout.spawn_bot_loading, parent, false);
                SpawnChatLoadingViewHolder spawnChatLoadingViewHolder = new SpawnChatLoadingViewHolder(viewLoading);
                return spawnChatLoadingViewHolder;

            case ChatViewTypes.CHAT_VIEW_CARD:
                View cardView = LayoutInflater.from(context).inflate(R.layout.spawn_chat_card, parent, false);
                SpawnChatCardViewHolder spawnChatCardViewHolder = new SpawnChatCardViewHolder(cardView);
                return spawnChatCardViewHolder;

            case ChatViewTypes.CHAT_VIEW_WIKI:
                View wikiView = LayoutInflater.from(context).inflate(R.layout.spawn_wiki_view, parent, false);
                SpawnWikiViewHolder wikiViewHolder = new SpawnWikiViewHolder(wikiView);
                return wikiViewHolder;

            case ChatViewTypes.CHAT_VIEW_NEWS:
                View newsView = LayoutInflater.from(context).inflate(R.layout.spawn_news_layout, parent, false);
                SpawnChatNewsHolder spawnChatNewsHolder = new SpawnChatNewsHolder(newsView);
                return spawnChatNewsHolder;

            case ChatViewTypes.CHAT_VIEW_WEB:
                View webView = LayoutInflater.from(context).inflate(R.layout.spawn_web_view, parent, false);
                SpawnWebSearchHolder spawnWebSearchHolder = new SpawnWebSearchHolder(webView);
                return spawnWebSearchHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (chatMessageType.get(position).getViewType()) {
            case ChatViewTypes.CHAT_VIEW_USER:
                SpawnChatUserViewHolder spawnChatUserViewHolder = (SpawnChatUserViewHolder) holder;
                String message = chatMessageType.get(position).getMessage();
                String date = chatMessageType.get(position).getDate();
                spawnChatUserViewHolder.user_message.setText(message);
                spawnChatUserViewHolder.user_time.setText(date);

                break;
            case ChatViewTypes.CHAT_VIEW_BOT:

                SpawnChatBotViewHolder spawnChatBotViewHolder = (SpawnChatBotViewHolder) holder;
                String botMessage = chatMessageType.get(position).getMessage();
                String botDate = chatMessageType.get(position).getDate();
                spawnChatBotViewHolder.bot_message.setText(botMessage);
                spawnChatBotViewHolder.bot_time.setText(botDate);

                if (iBotObserver != null
                        && !chatMessageType.get(position).getAction().equals("shutup")
                        && !chatMessageType.get(position).isSpeakFinish()
                        && SharedPreferenceUtility.getInstance(context).getPreference("speak")) {
                    chatMessageType.get(position).setSpeakFinish(true);
                    iBotObserver.speakBot(botMessage);
                } else {
                    chatMessageType.get(position).setSpeakFinish(true);
                }

                if (iBotObserver != null && !chatMessageType.get(position).isMessageAdded()) {
                    chatMessageType.get(position).setMessageAdded(true);
                    chatMessageType.get(position).setShortMessage(chatMessageType.get(position).getMessage());
                    iBotObserver.setChatMessage(chatMessageType.get(position));

                }

                if (chatMessageType.get(position).getAction() != null &&
                        !chatMessageType.get(position).getAction().isEmpty() &&
                        !chatMessageType.get(position).isActionCompleted()) {
                    chatMessageType.get(position).setActionCompleted(true);
                    iBotObserver.setAction(chatMessageType.get(position).getAction(), null);
                }

                break;

            case ChatViewTypes.CHAT_VIEW_LOADING:
                SpawnChatLoadingViewHolder spawnChatLoadingViewHolder = (SpawnChatLoadingViewHolder) holder;
                spawnChatLoadingViewHolder.loading.setVisibility(View.VISIBLE);
                break;

            case ChatViewTypes.CHAT_VIEW_CARD:
                final SpawnChatCardViewHolder spawnChatCardViewHolder = (SpawnChatCardViewHolder) holder;
                spawnChatCardViewHolder.spawn_card_text.setText(chatMessageType.get(position).getMessage());
                spawnChatCardViewHolder.card_button.setText(chatMessageType.get(position).getButtonText());
                final int pos = position;
                spawnChatCardViewHolder.card_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatMessageType.get(position).setActionCompleted(true);
                        iBotObserver.setAction(chatMessageType.get(pos).getAction(), null);

                    }
                });

                if (iBotObserver != null
                        && !chatMessageType.get(position).isSpeakFinish()
                        && SharedPreferenceUtility.getInstance(context).getPreference("speak")) {
                    chatMessageType.get(position).setSpeakFinish(true);
                    iBotObserver.speakBot(chatMessageType.get(position).getMessage());
                } else {
                    chatMessageType.get(position).setSpeakFinish(true);
                }

                if (iBotObserver != null && !chatMessageType.get(position).isMessageAdded()) {
                    chatMessageType.get(position).setMessageAdded(true);
                    chatMessageType.get(position).setShortMessage(chatMessageType.get(position).getMessage());
                    iBotObserver.setChatMessage(chatMessageType.get(position));

                }

                break;

            case ChatViewTypes.CHAT_VIEW_WIKI:
                final SpawnWikiViewHolder spawnWikiViewHolder = (SpawnWikiViewHolder) holder;
                final SpawnWikiModel spawnWikiModel = chatMessageType.get(position).getSpawnWikiModel();
                spawnWikiViewHolder.wikiTitle.setText(AppUtils.getInstance().getInfoFromExtract(chatMessageType.get(position).getSpawnWikiModel().getExtract(), "info"));

                if (chatMessageType.get(position).getSpawnWikiModel().getThumbnail() != null) {
                    Glide.with(context)
                            .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.thumbnail_not_found).error(R.drawable.thumbnail_not_found))
                            .load(chatMessageType.get(position).getSpawnWikiModel().getThumbnail().getSource())
                            .apply(RequestOptions.circleCropTransform())
                            .into(spawnWikiViewHolder.wikiImage);
                } else {
                    Glide.with(context)
                            .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.thumbnail_not_found).error(R.drawable.thumbnail_not_found))
                            .load(R.drawable.thumbnail_not_found)
                            .apply(RequestOptions.circleCropTransform())
                            .into(spawnWikiViewHolder.wikiImage);
                }
                if (chatMessageType.get(position).getSpawnWikiModel().getTitle().length() > 2) {
                    spawnWikiViewHolder.wikiParagraph.setText(spawnWikiModel.getTitle());
                    spawnWikiViewHolder.wikiButton.setText(spawnWikiModel.getTitle());
                } else {
                    spawnWikiViewHolder.wikiParagraph.setText(spawnWikiModel.getTitle() + " - " +
                            spawnWikiModel.getDescription()
                    );
                    spawnWikiViewHolder.wikiButton.setText(spawnWikiModel.getTitle() + " - " + spawnWikiModel.getDescription());
                }
                spawnWikiViewHolder.wikiButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatMessageType.get(position).setActionCompleted(true);
                        iBotObserver.setAction("web_action", spawnWikiModel);

                    }
                });

                spawnWikiViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatMessageType.get(position).setActionCompleted(true);
                        iBotObserver.setAction("web_action", spawnWikiModel);
                    }
                });

                if (iBotObserver != null
                        && !chatMessageType.get(position).isSpeakFinish()
                        && SharedPreferenceUtility.getInstance(context).getPreference("speak")) {
                    chatMessageType.get(position).setSpeakFinish(true);
                    iBotObserver.speakBot(AppUtils.getInstance().getInfoFromExtract(chatMessageType.get(position).getSpawnWikiModel().getExtract(), "speak"));
                } else {
                    chatMessageType.get(position).setSpeakFinish(true);
                }

                if (iBotObserver != null && !chatMessageType.get(position).isMessageAdded()) {
                    chatMessageType.get(position).setMessageAdded(true);
                    chatMessageType.get(position).setShortMessage(AppUtils.getInstance().getInfoFromExtract(chatMessageType.get(position).getSpawnWikiModel().getExtract(), "info"));
                    iBotObserver.setChatMessage(chatMessageType.get(position));
                }

                break;

            case ChatViewTypes.CHAT_VIEW_WEB:
                final SpawnWebSearchHolder spawnWebSearchHolder = (SpawnWebSearchHolder) holder;
                WebSearchResults webSearchResults = chatMessageType.get(position).getChatCardModel().getWebSearchResults();
                final ArrayList<ValueResults> valueResults = webSearchResults.getWebPages().getValue();

                if (iBotObserver != null
                        && !chatMessageType.get(position).isSpeakFinish()
                        && SharedPreferenceUtility.getInstance(context).getPreference("speak")) {
                    chatMessageType.get(position).setSpeakFinish(true);
                    iBotObserver.speakBot(chatMessageType.get(position).getMessage());
                } else {
                    chatMessageType.get(position).setSpeakFinish(true);
                }
                spawnWebSearchHolder.webRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                SpawnWebSearchAdapter spawnWebSearchAdapter = new SpawnWebSearchAdapter(context, valueResults);
                spawnWebSearchHolder.webRecycler.setAdapter(spawnWebSearchAdapter);
                spawnWebSearchAdapter.notifyDataSetChanged();

                break;

            case ChatViewTypes.CHAT_VIEW_NEWS:
                final SpawnChatNewsHolder spawnChatNewsHolder = (SpawnChatNewsHolder) holder;
                ArrayList<NewsValue> news = chatMessageType.get(position).getChatCardModel().getNews().getValue();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                spawnChatNewsHolder.newRecycler.setLayoutManager(linearLayoutManager);
                SpawnNewsAdapter spawnNewsAdapter = new SpawnNewsAdapter(context, news);
                spawnChatNewsHolder.newRecycler.setAdapter(spawnNewsAdapter);
                spawnNewsAdapter.notifyDataSetChanged();

                break;
        }

    }

    @Override
    public int getItemCount() {
        return chatMessageType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessageType.get(position).getViewType();
    }

}
