package com.assistance.Users.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "userregistration")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String username;
    private String email;
    private String password;
    private String phoneNumber;

    @Column(nullable = true)
    private Timestamp createdAt;

    @Column(name = "preferences", columnDefinition = "TEXT")
    private String preferences;

    public UserModel() {
    }

    public UserModel(String username, String email, String password, String phoneNumber, Timestamp createdAt, String preferences) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.preferences = preferences;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
