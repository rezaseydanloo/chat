package com.example.Client;

import java.io.*;
import java.net.*;

import com.example.MessageClasses.*;
import com.google.gson.*;



public class TCPClient {
    private Socket socket;
    private DataInputStream recieveBuffer;
    private DataOutputStream sendBuffer;
    private String serverIP;
    private int serverPort;
    
    private Gson gsonAgent;

    private String username, password;
    private String bio;
    private String token;

    private ServerMessage lastServerMessage;

    public TCPClient(String serverIP, int serverPort) {
        GsonBuilder builder = new GsonBuilder();
        this.gsonAgent = builder.create();
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    private boolean establishConnection() {
        try {
            socket = new Socket(serverIP, serverPort);
            sendBuffer = new DataOutputStream(
                socket.getOutputStream()
            );
            recieveBuffer = new DataInputStream(
                socket.getInputStream()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Unable to initialize socket!");
            e.printStackTrace();
            return false;
        }
    }

    private boolean endConnection() {
        if(socket == null) return true;
        try {
            socket.close();
            recieveBuffer.close();
            sendBuffer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean sendMessage(String message) {
        try {
            sendBuffer.writeUTF(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String recieveResponse() {
        try {
            return recieveBuffer.readUTF();
        } catch (IOException e) {
            return null;
        }
    }


    public boolean setBio(String newBio) {
        SetBioMessage setBioMessage = new SetBioMessage(newBio);
        setBioMessage.setToken(token);

        establishConnection();
        sendMessage(gsonAgent.toJson(setBioMessage));
        lastServerMessage = gsonAgent.fromJson(
            recieveResponse(), ServerMessage.class);
        endConnection();
        if (lastServerMessage.wasSuccessfull())
            this.bio = newBio;
        return lastServerMessage.wasSuccessfull();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        GetBioMessage getBioMessage = new GetBioMessage();
        getBioMessage.setToken(token);
        try {
            establishConnection();
            sendMessage(gsonAgent.toJson(getBioMessage));
            lastServerMessage = gsonAgent.fromJson(
                recieveResponse(), ServerMessage.class);
            endConnection();
            if (lastServerMessage.wasSuccessfull()) {
                this.bio = lastServerMessage.getAdditionalInfo();
                return this.bio;
            }
            else
                return null;

        } catch (Exception e) {
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ServerMessage getLastServerMessage() {
        return lastServerMessage;
    }

    private boolean signupLogin(boolean newUser) {
        SignupLoginMessage loginMessage = new SignupLoginMessage(username, password, newUser);
        try {
            establishConnection();
            sendMessage(gsonAgent.toJson(loginMessage));
            lastServerMessage = gsonAgent.fromJson(
                recieveResponse(), ServerMessage.class);
            endConnection();
            boolean success = lastServerMessage.wasSuccessfull();
            if (success) {
                this.token = lastServerMessage.getAdditionalInfo();
                return true;
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean login() {
        return signupLogin(false);
    }

    public boolean signup() {
        return signupLogin(true);
    }

    public void logout() {
        LogoutMessage logoutMessage = new LogoutMessage();
        logoutMessage.setToken(token);

        establishConnection();
        sendMessage(gsonAgent.toJson(logoutMessage));
        endConnection();
    }
}