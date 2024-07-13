package com.example.MessageClasses;


public class ServerMessage {
    private boolean success;
    private String additionalInfo;

    public ServerMessage (boolean success, String info) {
        this.success = success;
        this.additionalInfo = info;
    }

    public boolean wasSuccessfull () {
        return this.success;
    }

    public String getAdditionalInfo () {
        return this.additionalInfo;
    }
}