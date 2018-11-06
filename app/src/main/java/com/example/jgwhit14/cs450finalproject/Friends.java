package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class Friends extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList friendsList;
    private FriendsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewFriends);
        friendsList = new ArrayList<>();


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));


        //retrieve friends list from firebase and create Friend objects
        ArrayList locations = new ArrayList<MyLocations>();
        MyLocationsObject friend1Locations = new MyLocationsObject();

        FriendObject friend1 = new FriendObject("wazzza","morakh1@stlawu.edu",locations);
        FriendObject friend2 = new FriendObject("gordon","gordonwhite636@gmail.com",locations);

        friendsList.add(friend1);
        friendsList.add(friend2);

        //update recycler view adapter
        mAdapter = new FriendsAdapter(friendsList, "test",this);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void share (View view){



    }

    public void friends (View view){

        Intent intent = new Intent(this, Friends.class);
        startActivity(intent);
    }

    public void locations (View view){

        Intent intent = new Intent(this, MyLocations.class);
        startActivity(intent);

    }
}
