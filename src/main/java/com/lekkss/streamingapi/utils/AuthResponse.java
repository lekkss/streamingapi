package com.lekkss.streamingapi.utils;

import lombok.*;

public class AuthResponse {
    private String token;
    private boolean status;
    private  String message;

    public AuthResponse(String token, boolean status, String message) {
        this.token = token;
        this.status = status;
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
