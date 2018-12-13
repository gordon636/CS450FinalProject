package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewRecommendations extends AppCompatActivity {
    private ArrayList locationsList;
    ListView myList;
    private TextView radius;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recommendations);
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();

        locationsList = new ArrayList<>();

        final Intent i = this.getIntent();
        locationsList = i.getExtras().getStringArrayList("Locations");
        radius = findViewById(R.id.textViewRadius);
        radius.setText(pref.getInt("locationRadius",30));

        //myList.setAdapter();

        System.out.println("LOCATIONS LIST " +locationsList);

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationsList);

        ListView myListView = findViewById(R.id.listViewLocations);
        myListView.setAdapter(itemsAdapter);

    }
}
