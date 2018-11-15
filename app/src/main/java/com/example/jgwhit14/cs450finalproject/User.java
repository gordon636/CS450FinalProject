package com.example.jgwhit14.cs450finalproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;

@IgnoreExtraProperties
public class User {

    private DatabaseReference mDatabase;

    public String email;
    public String password;
    public String name;
    public String username;
    public ArrayList<String> locations;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // For creating new users
    public User(String email, String password, String name, String username) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.username = username;
    }


    public void writeNewUser(User user) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(user.username).child("name").setValue(user.name);
        mDatabase.child("users").child(user.username).child("password").setValue(user.password);
        mDatabase.child("users").child(user.username).child("email").setValue(user.email);

    }

}
