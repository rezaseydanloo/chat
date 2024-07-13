package com.example.Client;

import java.util.Scanner;

public class ClientView {
    private static Scanner scanner = new Scanner(System.in);
    private static TCPClient client;

    static boolean login () {
        System.out.println("username:");
        String username = scanner.nextLine();
        System.out.println("password:");
        String password = scanner.nextLine();
        client.setUsername(username);
        client.setPassword(password);
        if (client.login()) {
            System.out.println("Login successfull!");
            return true;
        } else {
            String problem = client.getLastServerMessage().getAdditionalInfo();
            System.out.println("Unsuccessfull login: " + problem);
            return false;
        }
    }

    static boolean signup () {
        System.out.println("username:");
        String username = scanner.nextLine();
        System.out.println("password:");
        String password = scanner.nextLine();
        client.setUsername(username);
        client.setPassword(password);
        if (client.signup()) {
            System.out.println("Signup successfull!");
            return true;
        } else {
            System.out.print("There was a problem in the signup procedure: ");
            System.out.println(client.getLastServerMessage().getAdditionalInfo());
            return false;
        }
    }

    static void logout () {
        client.logout();
    }

    static void changeBio () {
        System.out.print("Enter your new bio: ");
        String newBio = scanner.nextLine();
        if (client.setBio(newBio)) {
            System.out.println("Bio changed successfully");
        } else {
            System.out.println("Cannot change bio :(");
        }
    }

    static void run () {
        System.out.println("Welcome!");
        int command = 0;
        boolean requiresLogin = true;
        while (true) {
            while (requiresLogin) {
                do {
                    System.out.println("Enter 0 to login, 1 to signup, 2 to exit: ");
                    command = Integer.parseInt(scanner.nextLine());
                } while (command < 0 || command > 2);
                if (command == 2) {
                    System.out.println("See You!");
                    System.exit(0);
                }
                if (command == 1 && signup()) requiresLogin = false;
                if (command == 0 && login()) requiresLogin = false;
            }

            while (true) {
                String bio = client.getBio();
                if (bio == null) {
                    System.out.println("You currently have no bio!");
                } else {
                    System.out.println("Your current bio:");
                    System.out.println(bio);
                }
                do {
                    System.out.println("Enter 0 to logout, 1 to change bio");
                    command = Integer.parseInt(scanner.nextLine());
                } while (command < 0 || command > 1);
                if (command == 1) changeBio();
                else break;
            }
            System.out.println("Goodbye, " + client.getUsername());
            logout();
            requiresLogin = true;
        }
    }

    public static void main(String[] args) {
        client = new TCPClient("localhost", 5000);
        run();
    }
    
}