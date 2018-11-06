package com.example.jgwhit14.cs450finalproject;

import android.location.Location;

/**
 * Class that represent the message.
 */
public class MyLocationsObject {
    public String username;
    public String date;
    public String time;
    public Location myLocation;
    public int id;

    public MyLocationsObject() {
    }

    public MyLocationsObject(String username, Location myLocatoin, String date, String time) {
        this.username = username;
        this.myLocation = myLocatoin;
        this.date = date;
        this.time = time;
    }

    public Location getCoordinates() {
        return myLocation;
    }

    public void setMyLocation(Double lat,Double lon) {
        this.myLocation.setLatitude(lat);
        this.myLocation.setLongitude(lon);
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
