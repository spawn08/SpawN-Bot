package com.spawn.ai.interfaces;

import com.spawn.ai.model.ChatCardModel;
import com.spawn.ai.model.SpawnWikiModel;

public interface IBotWikiNLP {

    public void showUI(ChatCardModel chatCardModel);

    public void showNotFound(SpawnWikiModel spawnWikiModel);

}
