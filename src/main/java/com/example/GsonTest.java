package com.example;

import java.util.ArrayList;

import com.google.gson.*;


class Soldier {
    private ArrayList<String> weapons;
    private int age;
    private String name;

    public Soldier () {
        this.weapons = new ArrayList<String>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public void addWeapon(String weapon) {
        this.weapons.add(weapon);
    }

    public void introduceYourself() {
        System.out.println("My name is " + name + ".");
        System.out.println("I am " + age + " years old.");
        System.out.println("My weapon(s):");
        for (String weapon : weapons) {
            System.out.println(weapon);
        }
    }
}

public class GsonTest {
    public static void main(String[] args) {
        Soldier s = new Soldier();
        s.setAge(30);
        s.setName("Somename");
        s.addWeapon("Knife");
        s.addWeapon("Sword");
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        /*String jsonString = gson.toJson(s);
        System.out.println(jsonString);*/

        String jsonString = " {\"weapons\": [\"Katiocha\"],\"age\": 29,\"name\": \"Some-other-name\"}";
        Soldier t = gson.fromJson(jsonString, Soldier.class);
        t.introduceYourself();
    }
}


/*
 * Google JSON
 */