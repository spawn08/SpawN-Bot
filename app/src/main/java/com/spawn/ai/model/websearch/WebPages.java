package com.spawn.ai.model.websearch;

import java.util.ArrayList;

public class WebPages {

    private ArrayList<ValueResults> value;
    private ArrayList<DeepLinks> deepLinks;

    public ArrayList<ValueResults> getValue() {
        return value;
    }

    public void setValue(ArrayList<ValueResults> value) {
        this.value = value;
    }

    public ArrayList<DeepLinks> getDeepLinks() {
        return deepLinks;
    }

    public void setDeepLinks(ArrayList<DeepLinks> deepLinks) {
        this.deepLinks = deepLinks;
    }
}
