package com.spawn.ai.model;

import java.util.ArrayList;

public class Entities {

    private String tag;
    private String entity;
    private ArrayList<String> value;
    private ArrayList<NotablePerson> notable_person;
    private ArrayList<Location> location;

    private ArrayList<Intent> intent;

    public ArrayList<Intent> getBotIntents() {
        return intent;
    }

    public void setBotIntents(ArrayList<Intent> botIntents) {
        this.intent = botIntents;
    }

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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public ArrayList<NotablePerson> getNotable_person() {
        return notable_person;
    }

    public void setNotable_person(ArrayList<NotablePerson> notable_person) {
        this.notable_person = notable_person;
    }

    public ArrayList<Location> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Location> location) {
        this.location = location;
    }
}
