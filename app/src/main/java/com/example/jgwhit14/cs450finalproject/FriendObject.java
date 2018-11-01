package com.example.jgwhit14.cs450finalproject;

import android.location.Location;

import java.util.ArrayList;

/**
 * Class that represent the message.
 */
public class FriendObject {
    public String username;
    public String email;
    ArrayList locations;
    public int id;

    public FriendObject() {
    }

    public FriendObject(String username, String email, ArrayList locations) {
        this.username = username;
        this.email = email;
        this.locations = locations;

    }

    public ArrayList getLocations() {
        return locations;
    }



    public String getUsername() {
        return username;
    }
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
