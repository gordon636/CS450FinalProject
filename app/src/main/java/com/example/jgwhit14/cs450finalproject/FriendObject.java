package com.example.jgwhit14.cs450finalproject;

import android.location.Location;

import java.util.ArrayList;

/**
 * Class that represent the message.
 */
public class FriendObject {
    public String username;
    String approved ;
    ArrayList locations;
    public int id;


    public FriendObject(String username, String approved) {
        this.username = username;
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
