package com.example.jgwhit14.cs450finalproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private DatabaseReference mDatabase;

    public String email;
    public String password;
    public String name;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // For creating new users
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // For importing into the DB
    private User(String password, String name){
        this.password = password;
        this.name = name;
    }

    public void writeNewUser(User user) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(user.email).child("name").setValue(user.name);
        mDatabase.child("users").child(user.email).child("password").setValue(user.password);
        mDatabase.child("users").child(user.email).child("email").setValue(user.email);

    }

}
