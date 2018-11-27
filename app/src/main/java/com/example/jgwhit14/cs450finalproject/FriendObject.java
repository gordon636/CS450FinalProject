package com.example.jgwhit14.cs450finalproject;

import android.location.Location;

import java.util.ArrayList;

/**
 * Class that represent the message.
 */
public class FriendObject {
    public String username;
    public String dateAdded;
    String approved ;
    ArrayList locations;
    public int id;

    public FriendObject() {
    }

    public FriendObject(String username, String dateAdded, String approved) {
        this.username = username;
        this.dateAdded = dateAdded;
        this.approved = approved;

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
