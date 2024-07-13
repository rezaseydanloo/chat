package com.example.MessageClasses;

public class LogoutMessage extends ClientMessage {
    public LogoutMessage () {
        this.type = ClientMessageType.logout;
    }
}