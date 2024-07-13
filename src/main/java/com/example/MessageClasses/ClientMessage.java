package com.example.MessageClasses;

public class ClientMessage {
    protected String token;
    protected ClientMessageType type;

    public String getToken() {
        return token;
    }

    public ClientMessageType getType() {
        return type;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


