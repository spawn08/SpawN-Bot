package com.spawn.ai.model;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Entities {

    private String tag;
    private String entity;
    private ArrayList<String> value;
    private ArrayList<NotablePerson> notable_person;
    private ArrayList<Location> location;
    private ArrayList<Intent> intent;
}
