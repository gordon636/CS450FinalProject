package com.example.jgwhit14.cs450finalproject;

public class FriendRequestObject {

    public String username;
    public int id;


    public FriendRequestObject(String username) {
        this.username = username;
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
