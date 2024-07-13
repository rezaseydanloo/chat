package com.example.MessageClasses;

public class GetBioMessage extends ClientMessage {
    public GetBioMessage () {
        this.type = ClientMessageType.getbio;
    }
}