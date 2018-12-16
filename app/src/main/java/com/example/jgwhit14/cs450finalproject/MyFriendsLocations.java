package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyFriendsLocations extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LocationsAdapter mAdapter;
    private ArrayList locationsList;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String username;
    private TextView userInfo;
    private FirebaseDatabase database;
    private String loggedInUser;
    private TextView view_ALL;
    private TextView undo;

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


        database  = FirebaseDatabase.getInstance();
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        loggedInUser = pref.getString("Username","none");

        view_ALL = findViewById(R.id.textView6);
        view_ALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        undo = findViewById(R.id.textView7);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        //retrieve locations from Firebase and create MyLocationsObject objects
        DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    //loggedInUser
                    if(usernameP.equals(username

                    )){
                        User loginUser = user.getValue(User.class);
                        ArrayList<String> userLocations = loginUser.locations;
                        System.out.println("LOCATIONS: " + userLocations);



                        for(String aLocation:userLocations){
                            if(aLocation == null){
                                continue;
                            }
                            String[] aLocationArr = aLocation.split("mySPLIT");

                            Location location = new Location("");
                            location.setLatitude(Double.parseDouble(aLocationArr[0]));
                            location.setLongitude(Double.parseDouble(aLocationArr[1]));

                            MyLocationsObject locationToList = new MyLocationsObject(loggedInUser, location, aLocationArr[5], aLocationArr[6], aLocationArr[2]);

                            locationsList.add(0,locationToList);//add latest one to start of list
                        }


                        break;
                    }



                }

                //update recycler view adapter
                mAdapter = new LocationsAdapter(MyFriendsLocations.this, locationsList, "test");
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public void show(){
        editor.putString("FRIEND_LOCATION_SHOW", username).apply();
        finish();
    }
}

