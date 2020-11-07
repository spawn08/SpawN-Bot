package com.spawn.ai.model;

import lombok.Data;

@Data
public class BotResponse {

    private String msg_id;
    private String _text;
    private Entities entities;
    private Intent intent;
}
