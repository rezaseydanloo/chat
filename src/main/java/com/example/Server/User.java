package com.example.Server;

import java.util.HashMap;

public class User {
    private String username, password;
    private String bio;
    private String currentToken;
    
    private static HashMap <String, User> allUsersByUsername = new HashMap <String, User> ();
    private static HashMap <String, User> allUsersByToken = new HashMap <String, User> ();

    public User (String username, String password) throws Exception {
        if (findUserByUsername (username) != null) {
            throw new Exception ("Username already exists: " + username);
        }
        this.username = username;
        this.password = password;
        allUsersByUsername.put (username, this);
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public void setCurrentToken (String currentToken) {
        if (this.currentToken != null)
            allUsersByToken.remove (this.currentToken);
        this.currentToken = currentToken;
        if (currentToken != null) 
            allUsersByToken.put (currentToken, this);
    }

    public void setBio(String bio) {
        if (bio != null && bio.isEmpty())
            bio = null;
        this.bio = bio;
    }

    public String getUsername () {
        return username;
    }
    
    public String getPassword () {
        return password;
    }

    public String getCurrentToken () {
        return currentToken;
    }

    public String getBio() {
        return bio;
    }

    public static User findUserByUsername (String username) {
        return allUsersByUsername.get (username);
    }

    public static User findUserByToken (String token) {
        return allUsersByToken.get (token);
    }
    
}