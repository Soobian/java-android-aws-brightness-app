package com.example.brightnessapp.ui.noweLogowanie;


public class LoggedUser {

    private final String userId;
    private final String displayName;
    private final String email;

    public LoggedUser(String userId, String displayName, String email) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return displayName;
    }
}