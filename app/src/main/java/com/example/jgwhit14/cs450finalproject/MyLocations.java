package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class MyLocations extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private LocationsAdapter mAdapter;
    private ArrayList locationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locations);

        mRecyclerView = (RecyclerView) findViewById(R.id.recylcerViewLoactions);
        locationsList = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));


        //retrieve locations from firebase and create MyLocationsObject objects
        Location location1 = new Location("");
        location1.setLatitude(0);
        location1.setLongitude(0);

        Location location2 = new Location("");
        location2.setLatitude(10);
        location2.setLongitude(15);


        MyLocationsObject locationO1 = new MyLocationsObject("wazzza",location1,"01/12/18","8.15pm");
        MyLocationsObject locationO2 = new MyLocationsObject("gordon",location2,"01/12/18","8.15pm");

        locationsList.add(locationO1);
        locationsList.add(locationO2);

        //update recycler view adapter
        mAdapter = new LocationsAdapter(this,locationsList, "test");
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
