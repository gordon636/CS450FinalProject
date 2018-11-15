package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SaveLocation extends AppCompatActivity {
    private ArrayList<Location> myList;
    private Button Save;
    private Button Cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);

        Save = findViewById(R.id.button3);
        Cancel = findViewById(R.id.button);
        final Intent i = this.getIntent();

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference AddLocation = FirebaseDatabase.getInstance().getReference();
                myList = i.getParcelableArrayListExtra("list");
                Location currentLocation = i.getExtras().getParcelable("location");
                myList.add(currentLocation);
                String username = i.getExtras().getString("username");

                AddLocation.child("users").child(username).child("locations").child(Integer.toString(
                        (myList.indexOf(currentLocation)))).setValue(currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}

