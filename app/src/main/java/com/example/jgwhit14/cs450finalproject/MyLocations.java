package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Stack;

public class MyLocations extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private RecyclerView mRecyclerView;
    private LocationsAdapter mAdapter;
    private ArrayList locationsList;
    private SharedPreferences pref;
    private FirebaseDatabase database;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recylcerViewLoactions);
        locationsList = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));


        database  = FirebaseDatabase.getInstance();
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        loggedInUser = pref.getString("Username","none");
        //click on my location
        pref.edit().putString("friend","false").apply();

        //retrieve locations from Firebase and create MyLocationsObject objects
        DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    //loggedInUser
                    if(usernameP.equals(loggedInUser)) {
                        User loginUser = user.getValue(User.class);
                        ArrayList<String> userLocations = loginUser.locations;
                        System.out.println("LOCATIONS: " + userLocations);

                        if (userLocations != null) {
                            for (String aLocation : userLocations) {
                                if (aLocation == null) {
                                    continue;
                                }
                                String[] aLocationArr = aLocation.split("mySPLIT");

                                Location location = new Location("");
                                location.setLatitude(Double.parseDouble(aLocationArr[0]));
                                location.setLongitude(Double.parseDouble(aLocationArr[1]));

                                MyLocationsObject locationToList = new MyLocationsObject(loggedInUser, location, aLocationArr[5], aLocationArr[6], aLocationArr[2],aLocationArr[3]);

                                locationsList.add(0, locationToList);//add latest one to start of list
                            }


                            break;
                        }
                    }



                }

                //update recycler view adapter
                mAdapter = new LocationsAdapter(MyLocations.this, locationsList, "test");
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_locations) {
            //click on my location
            pref.edit().putString("friend","false").apply();

            locations();
        } else if (id == R.id.nav_friends) {
            friends();
        } else if (id == R.id.nav_requests) {
            Intent intent = new Intent(this, FriendRequests.class);
            startActivity(intent);

        } else if (id == R.id.nav_add) {
            add();
        }
        else if (id == R.id.nav_share) {

            //click on my location
            pref.edit().putString("friend","false").apply();

            Toast.makeText(getApplicationContext(),"Select a location to share!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MyLocations.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            Setting();

        }
        else
        if (id == R.id.nav_logout) {

            finish();
            //
            pref.edit().putString("Username", "").apply();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void share (View view){

        Toast.makeText(getApplicationContext(),"Select a location to share!",Toast.LENGTH_LONG).show();


    }

    public void friends (View view){

        Intent intent = new Intent(this, Friends.class);
        startActivity(intent);
    }

    public void locations (View view){

        Intent intent = new Intent(this, MyLocationsObject.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //click on friends location
        pref = getApplicationContext().getSharedPreferences("Profile",0);

        pref.edit().putString("friend","false").apply();


    }


    private void add() {

        Intent intent = new Intent(this,AddFriend.class);
        startActivity(intent);
    }

    private void add (View view){

        add();
    }

    private void Setting(){
        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }
    public void locations (){

        Intent intent = new Intent(this, MyLocations.class);
        startActivity(intent);
    }
    public void friends (){

        Intent intent = new Intent(this, Friends.class);
        startActivity(intent);
    }
}
