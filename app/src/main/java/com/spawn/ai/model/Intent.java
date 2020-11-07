package com.spawn.ai.model;

import lombok.Data;

@Data
public class Intent {

    private String name;
    private String value;
    private double confidence;
}
