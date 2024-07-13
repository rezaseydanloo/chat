package com.example.MessageClasses;

public class SignupLoginMessage extends ClientMessage {
    private String username;
    private String password;
    public boolean newUser;
    
    public SignupLoginMessage (String username, String password, boolean newUser) {
        this.type = ClientMessageType.signupLogin;
        this.username = username;
        this.password = password;
        this.newUser  = newUser;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}