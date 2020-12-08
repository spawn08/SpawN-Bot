package com.spawn.ai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.spawn.ai.R;
import com.spawn.ai.constants.ChatViewTypes;
import com.spawn.ai.interfaces.IBotObserver;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.model.wiki.SpawnWikiModel;
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

import static com.spawn.ai.constants.AppConstants.SPEAK;
import static com.spawn.ai.constants.AppConstants.WEB_ACTION;

public class SpawnChatbotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private ArrayList<ChatMessageType> chatMessageType;
    private final IBotObserver iBotObserver;
    private final AppUtils appUtils;

    public SpawnChatbotAdapter(Context context, ArrayList<ChatMessageType> chatMessageType, AppUtils appUtils) {
        this.context = context;
        this.chatMessageType = chatMessageType;
        iBotObserver = (IBotObserver) context;
        this.appUtils = appUtils;
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
                return new SpawnChatUserViewHolder(view);

            case ChatViewTypes.CHAT_VIEW_LOADING:
                View viewLoading = LayoutInflater.from(context).inflate(R.layout.spawn_bot_loading, parent, false);
                return new SpawnChatLoadingViewHolder(viewLoading);

            case ChatViewTypes.CHAT_VIEW_CARD:
                View cardView = LayoutInflater.from(context).inflate(R.layout.spawn_chat_card, parent, false);
                return new SpawnChatCardViewHolder(cardView);

            case ChatViewTypes.CHAT_VIEW_WIKI:
                View wikiView = LayoutInflater.from(context).inflate(R.layout.spawn_wiki_view, parent, false);
                return new SpawnWikiViewHolder(wikiView);

            case ChatViewTypes.CHAT_VIEW_NEWS:
                View newsView = LayoutInflater.from(context).inflate(R.layout.spawn_news_layout, parent, false);
                return new SpawnChatNewsHolder(newsView);

            case ChatViewTypes.CHAT_VIEW_WEB:
                View webView = LayoutInflater.from(context).inflate(R.layout.spawn_web_view, parent, false);
                return new SpawnWebSearchHolder(webView);

            default:
                View viewDefault = LayoutInflater.from(context).inflate(R.layout.spawn_chat_bot, parent, false);
                return new SpawnChatBotViewHolder(viewDefault);
        }
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
                        && SharedPreferenceUtility.getInstance(context).getPreference(SPEAK)) {
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
                    if (iBotObserver != null) {
                        iBotObserver.setAction(chatMessageType.get(position).getAction(), null);
                    }
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
                spawnChatCardViewHolder.card_button.setOnClickListener(view -> {
                    chatMessageType.get(position).setActionCompleted(true);
                    iBotObserver.setAction(chatMessageType.get(position).getAction(), null);
                });

                if (iBotObserver != null
                        && !chatMessageType.get(position).isSpeakFinish()
                        && SharedPreferenceUtility.getInstance(context).getPreference(SPEAK)) {
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
                spawnWikiViewHolder.wikiTitle.setText(appUtils.getInfoFromExtract(chatMessageType.get(position).getSpawnWikiModel().getExtract(), "info"));

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
                    spawnWikiViewHolder.wikiParagraph.setText(String.format("%s - %s", spawnWikiModel.getTitle(), spawnWikiModel.getDescription())
                    );
                    spawnWikiViewHolder.wikiButton.setText(String.format("%s - %s", spawnWikiModel.getTitle(), spawnWikiModel.getDescription()));
                }
                spawnWikiViewHolder.wikiButton.setOnClickListener(view -> {
                    chatMessageType.get(position).setActionCompleted(true);
                    iBotObserver.setAction(WEB_ACTION, spawnWikiModel);

                });

                spawnWikiViewHolder.cardView.setOnClickListener(view -> {
                    chatMessageType.get(position).setActionCompleted(true);
                    iBotObserver.setAction(WEB_ACTION, spawnWikiModel);
                });

                if (iBotObserver != null
                        && !chatMessageType.get(position).isSpeakFinish()
                        && SharedPreferenceUtility.getInstance(context).getPreference(SPEAK)) {
                    chatMessageType.get(position).setSpeakFinish(true);
                    iBotObserver.speakBot(appUtils.getInfoFromExtract(chatMessageType.get(position).getSpawnWikiModel().getExtract(), SPEAK));
                } else {
                    chatMessageType.get(position).setSpeakFinish(true);
                }

                if (iBotObserver != null && !chatMessageType.get(position).isMessageAdded()) {
                    chatMessageType.get(position).setMessageAdded(true);
                    chatMessageType.get(position).setShortMessage(appUtils.getInfoFromExtract(chatMessageType.get(position).getSpawnWikiModel().getExtract(), "info"));
                    iBotObserver.setChatMessage(chatMessageType.get(position));
                }

                break;

            case ChatViewTypes.CHAT_VIEW_WEB:
                final SpawnWebSearchHolder spawnWebSearchHolder = (SpawnWebSearchHolder) holder;
                WebSearchResults webSearchResults = chatMessageType.get(position).getChatCardModel().getWebSearchResults();
                final ArrayList<ValueResults> valueResults = webSearchResults.getWebPages().getValue();

                ArrayList<NewsValue> newsResult =
                        webSearchResults.getNews() != null ? webSearchResults.getNews().getValue() : null;
                if (iBotObserver != null
                        && !chatMessageType.get(position).isSpeakFinish()
                        && SharedPreferenceUtility.getInstance(context).getPreference(SPEAK)) {
                    chatMessageType.get(position).setSpeakFinish(true);
                    iBotObserver.speakBot(chatMessageType.get(position).getMessage());
                } else {
                    chatMessageType.get(position).setSpeakFinish(true);
                }

                spawnWebSearchHolder.webRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                // Web
                SpawnWebSearchAdapter spawnWebSearchAdapter = new SpawnWebSearchAdapter(context, valueResults, appUtils);
                spawnWebSearchHolder.webRecycler.setAdapter(spawnWebSearchAdapter);
                spawnWebSearchAdapter.notifyDataSetChanged();

                //News
                if (newsResult != null) {
                    spawnWebSearchHolder.newsList.setLayoutManager(new LinearLayoutManager(context));
                    SpawnNewsSearchAdapter spawnNewsListAdapter = new SpawnNewsSearchAdapter(context, newsResult, appUtils);
                    spawnWebSearchHolder.newsList.setAdapter(spawnNewsListAdapter);
                    spawnNewsListAdapter.notifyDataSetChanged();
                } else {
                    spawnWebSearchHolder.newsList.setVisibility(View.GONE);
                    spawnWebSearchHolder.newsText.setVisibility(View.GONE);
                }

                //Images
                if (webSearchResults.getImages() != null
                        && webSearchResults.getImages().getValue() != null) {
                    spawnWebSearchHolder.imageList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    SpawnImageResultAdapter spawnImageResultAdapter = new SpawnImageResultAdapter(context, webSearchResults.getImages().getValue());
                    spawnWebSearchHolder.imageList.setAdapter(spawnImageResultAdapter);
                    spawnImageResultAdapter.notifyDataSetChanged();
                } else {
                    spawnWebSearchHolder.imageList.setVisibility(View.GONE);
                    spawnWebSearchHolder.imageText.setVisibility(View.GONE);
                }

                //Video
                if (webSearchResults.getVideos() != null
                        && webSearchResults.getVideos().getValue() != null) {
                    spawnWebSearchHolder.videoList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    SpawnVideoResultAdapter spawnVideoResultAdapter = new SpawnVideoResultAdapter(context, webSearchResults.getVideos().getValue());
                    spawnWebSearchHolder.videoList.setAdapter(spawnVideoResultAdapter);
                    spawnVideoResultAdapter.notifyDataSetChanged();
                } else {
                    spawnWebSearchHolder.videoList.setVisibility(View.GONE);
                    spawnWebSearchHolder.videoText.setVisibility(View.GONE);
                }

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
