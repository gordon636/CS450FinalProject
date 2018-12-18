package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewRecommendations extends AppCompatActivity {
    private ArrayList locationsList;
    ListView myList;
    private TextView radius;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private RecommendLocationsAdapter mAdapter;
    private  RecyclerView mRecyclerView;

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
        radius.setText("Friends locations within "+String.valueOf(pref.getInt("locationRadius",30)+" km of you."));

        System.out.println("LOCATIONS LIST " +locationsList);


        mRecyclerView = findViewById(R.id.recylcerViewLoactions);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        //update recycler view adapter
        mAdapter = new RecommendLocationsAdapter(ViewRecommendations.this, locationsList,"test");
        mRecyclerView.setAdapter(mAdapter);





    }

    public void settings (View view){
        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
        finish();
    }
}
