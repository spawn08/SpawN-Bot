package com.spawn.ai.model;

import java.util.ArrayList;

public class Entities {

    private String tag;
    private ArrayList<String> value;

    //private ArrayList<Intent> intent;

    //public ArrayList<Intent> getBotIntents() {
    //    return intent;
    //}

    //public void setBotIntents(ArrayList<Intent> botIntents) {
    //    this.intent = botIntents;
    //}

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ArrayList<String> getValue() {
        return value;
    }

    public void setValue(ArrayList<String> value) {
        this.value = value;
    }
}
