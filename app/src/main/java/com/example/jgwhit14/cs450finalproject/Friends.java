package com.example.jgwhit14.cs450finalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Friends extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList friendsList;
    private FriendsAdapter mAdapter;
    private FirebaseDatabase database;
    private String loggedInUser;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String realLocation;
    private String friend ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewFriends);
        friendsList = new ArrayList<>();


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));

        loadFriends ();



    }

    private void loadFriends() {

        friendsList.clear();
        database  = FirebaseDatabase.getInstance();
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        loggedInUser = pref.getString("Username","none");

        //retrieve locations from Firebase and create MyLocationsObject objects
        DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    //loggedInUser
                    if(usernameP.equals(loggedInUser)){
                        User loginUser = user.getValue(User.class);
                        ArrayList<String> userFirends = loginUser.friends;
                        System.out.println("Friends: " + userFirends);


                        //check if i have friends added
                        if (userFirends != null){

                            //has friends, check friend to see if they added us back if so add to my friends
                            Toast.makeText(Friends.this, "I have friends", Toast.LENGTH_SHORT).show();

                            for(String aFirend:userFirends){
                                if(aFirend == null){
                                    continue;
                                }
                                String[] aLocationArr = aFirend.split("mySPLIT");
                                FriendObject friend = new FriendObject(aLocationArr[0], aLocationArr[1], aLocationArr[3]);

                                if (aLocationArr[3].equals("true")){
                                    friendsList.add(0,friend);//add latest one to start of list
                                }else {
                                    friendsList.add(friend);//add to end of list
                                }
                            }

                        }

                        break;
                    }



                }

                //update recycler view adapter
                mAdapter = new FriendsAdapter(friendsList, "test",Friends.this);
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

        Intent intent = new Intent(this, MyLocations.class);
        startActivity(intent);

    }
    public void add (View view){

        Intent intent = new Intent(this, AddFriend.class);
        startActivityForResult(intent,1234);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK){
            loadFriends();
        }
    }
}
