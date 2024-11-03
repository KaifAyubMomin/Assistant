package com.assistance.Users.model;

import java.sql.Timestamp;

public class ResponseOnLoginModel {

    private String message;
    private Integer userId;
    private String username;
    private String email;
    private String phoneNumber;
    private Timestamp createdAt;
    private String preferences;


    public ResponseOnLoginModel(String message , Integer userId, String username, String email, String phoneNumber, Timestamp createdAt, String preferences) {
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.preferences = preferences;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }


}
