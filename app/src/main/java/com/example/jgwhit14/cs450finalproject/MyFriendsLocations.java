package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MyFriendsLocations extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LocationsAdapter mAdapter;
    private ArrayList locationsList;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String username;
    private TextView userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends_locatoins);
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();
        mRecyclerView = (RecyclerView) findViewById(R.id.recylcerViewLoactions);
        locationsList = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));


        //download all of this users locations
        username = pref.getString("clicked_username","none");
        userInfo = (TextView)findViewById(R.id.textViewLocation2);
        userInfo.setText(username+"'s Locations - Where have they been?");



        Location location1 = new Location("");
        location1.setLatitude(0);
        location1.setLongitude(0);

        Location location2 = new Location("");
        location2.setLatitude(10);
        location2.setLongitude(15);


        //retrieve locations from firebase and create MyLocationsObject objects

        MyLocationsObject locationO1 = new MyLocationsObject("wazzza",location1,"01/12/18","8.15pm");
        MyLocationsObject locationO2 = new MyLocationsObject("gordon",location2,"01/12/18","8.15pm");

        locationsList.add(locationO1);
        locationsList.add(locationO2);
        locationsList.add(locationO1);
        locationsList.add(locationO2);
        locationsList.add(locationO1);
        locationsList.add(locationO2);

        //update recycler view adapter
        mAdapter = new LocationsAdapter(this, locationsList, "test");
        mRecyclerView.setAdapter(mAdapter);
    }

    public void share (View view){



    }

    public void friends (View view){

        Intent intent = new Intent(this, Friends.class);
        startActivity(intent);
    }

    public void locations (View view){

        Intent intent = new Intent(this, MyLocationsObject.class);
        startActivity(intent);

    }
}
