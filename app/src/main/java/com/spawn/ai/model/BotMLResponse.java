package com.spawn.ai.model;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BotMLResponse {

    private Intent intent;
    private ArrayList<Entities> entities;
    private String text;
    private String project;
    private String model;
    private String lang;
}

