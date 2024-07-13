package com.example.Server;

import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

import com.google.gson.*;   

import com.example.MessageClasses.*;

public class TCPServerWorker extends Thread{
    private static ServerSocket server;
    private static Gson gsonAgent;

    private static final String INTERNAL_ERROR = "internal server error";
    private static final String INVALID_USERNAME = "no user exists with such username";
    private static final String USERNAME_TAKEN = "this username is taken";
    private static final String INVALID_TOKEN = "this token belongs to no user";
    private static final String WRONG_PASSWORD = "wrong password";
    private static final String BUSY_USER = "user is already logged in";

    private static int WORKERS;

    private static ArrayList<Socket> connections;

    private DataOutputStream sendBuffer;
    private DataInputStream recieveBuffer;

    private static boolean setupServer(int portNumber, int workerNum) {
        try {
            server = new ServerSocket(portNumber);
            connections = new ArrayList <Socket>();
            WORKERS = workerNum;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TCPServerWorker() {
        GsonBuilder builder = new GsonBuilder();
        gsonAgent = builder.create();
    }

    public void listen() throws IOException {
        Socket socket;
        while (true) {
            socket = server.accept();
            synchronized (connections) {
                connections.add(socket);
                connections.notify();
            }
        }
    }

    @Override
    public void run() {
        Socket socket;
        while (true) {
            socket = null;
            synchronized (connections) {
                while (connections.isEmpty()) {
                    try {
                        connections.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                socket = connections.get(0);
                connections.remove(0);
            }
            if (socket != null) {
                handleConnection(socket);
            }
        }
    }

    private String generateNewToken() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++)
            sb.append((char) (random.nextInt(128)));
        return sb.toString();
    }

    private ClientMessage extractClientMessage(String clientStr) {
        try {
            ClientMessage clientMessage = gsonAgent.fromJson(clientStr, ClientMessage.class);
            switch (clientMessage.getType()) {
                case signupLogin:
                    return gsonAgent.fromJson(clientStr, SignupLoginMessage.class);
                case setbio:
                    return gsonAgent.fromJson(clientStr, SetBioMessage.class);
                case getbio:
                    return gsonAgent.fromJson(clientStr, GetBioMessage.class);
                case logout:
                    return gsonAgent.fromJson(clientStr, LogoutMessage.class);
                default:
                    return null;
            }
        }
        catch (Exception e) {
            return null;
        }
    }


    private boolean sendMessage(boolean success, String problem) {
        ServerMessage failureMessage = new ServerMessage(success, problem);
        String failureString = gsonAgent.toJson(failureMessage);
        try {
            sendBuffer.writeUTF(failureString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sendFailure(String problem) {
        return sendMessage(false, problem);
    }

    private boolean sendSuccess(String info) {
        return sendMessage(true, info);
    }

    private void sendBioToClient(GetBioMessage msg) {
        String token = msg.getToken();
        User user = User.findUserByToken(token);
        if (user == null) {
            sendFailure(INVALID_TOKEN);
            return;
        }
        sendSuccess(user.getBio());
    }

    private void updateBioOfClient(SetBioMessage msg) {
        String token = msg.getToken();
        User user = User.findUserByToken(token);
        if (user == null) {
            sendFailure(INVALID_TOKEN);
            return;
        }
        user.setBio(msg.getNewBio());
        sendSuccess(null);
    }

    private void loginClient(SignupLoginMessage msg) {
        if (msg.newUser) {
            sendFailure(INTERNAL_ERROR);
            return;
        }
        
        User user = User.findUserByUsername(msg.getUsername());
        if (user == null) {
            sendFailure(INVALID_USERNAME);
            return;
        }

        if (!user.getPassword().equals(msg.getPassword())) {
            sendFailure(WRONG_PASSWORD);
            return;
        }

        if (user.getCurrentToken() != null) {
            sendFailure(BUSY_USER);
            return;
        }

        user.setCurrentToken(generateNewToken());
        sendSuccess(user.getCurrentToken());

    }

    private void registerNewClient(SignupLoginMessage msg) {
        if (!msg.newUser) {
            sendFailure(INTERNAL_ERROR);
            return;
        }
        if (User.findUserByUsername(msg.getUsername()) != null) {
            sendFailure(USERNAME_TAKEN);
            return;
        }

        try {
            User newUser = new User(msg.getUsername(), msg.getPassword());
            newUser.setCurrentToken(generateNewToken());
            sendSuccess(newUser.getCurrentToken());
        }
        catch (Exception e) {
            sendFailure(INTERNAL_ERROR);
            return;
        }        
    }

    private void logoutClient(LogoutMessage msg) {
        String token = msg.getToken();
        User user = User.findUserByToken(token);
        if (user == null) {
            sendFailure(INVALID_TOKEN);
            return;
        }

        user.setCurrentToken(null);
        sendSuccess(null);
    }

    private void handleConnection(Socket socket) {
        String clientRequest;
        
        try {
            recieveBuffer = new DataInputStream(
                new BufferedInputStream(socket.getInputStream())
            );
            sendBuffer = new DataOutputStream(
                new BufferedOutputStream(socket.getOutputStream())
            );
            

            clientRequest = recieveBuffer.readUTF();
            ClientMessage msg = extractClientMessage(clientRequest);
            
            if (msg instanceof SignupLoginMessage) {
                if (((SignupLoginMessage) msg).newUser)
                    registerNewClient((SignupLoginMessage) msg);
                else
                    loginClient((SignupLoginMessage) msg);
            } else if (msg instanceof GetBioMessage) {
                sendBioToClient((GetBioMessage) msg);
            } else if (msg instanceof SetBioMessage) {
                updateBioOfClient((SetBioMessage) msg);
            } else if (msg instanceof LogoutMessage) {
                logoutClient((LogoutMessage) msg);
            } else {
                sendFailure(INTERNAL_ERROR);
            }

            sendBuffer.close();
            recieveBuffer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TCPServerWorker.setupServer(5000, 10);
            for (int i = 0; i < WORKERS; i++) {
                new TCPServerWorker().start();
            }
            new TCPServerWorker().listen();
        } catch (Exception e) {
            System.out.println("Server encountered a problem!");
            e.printStackTrace();
        }
    }
}