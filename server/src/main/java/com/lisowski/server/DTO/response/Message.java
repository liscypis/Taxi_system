package com.lisowski.server.DTO.response;

import lombok.Data;

@Data
public class Message {
    private String msg;

    public Message(String message) {
        this.msg = message;
    }
}
