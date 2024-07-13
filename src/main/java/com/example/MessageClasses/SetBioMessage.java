package com.example.MessageClasses;

public class SetBioMessage extends ClientMessage {
    private String newBio;
    
    public SetBioMessage (String newBio) {
        this.type = ClientMessageType.setbio;
        this.newBio = newBio;
    }

    public String getNewBio() {
        return newBio;
    }
}
