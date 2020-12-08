package com.spawn.ai.model.localdata;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;

@Data
public class PreprocessData {
    @SerializedName("words")
    private ArrayList<String> words;
    @SerializedName("classes")
    private ArrayList<String> classes;
}
