package com.assistance.Users.dto;

public class MessageDTO {

    private int id;
    private String message;

    public MessageDTO(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        message = message;
    }
}
