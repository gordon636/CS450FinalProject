package com.example.jgwhit14.cs450finalproject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AcceptFriend extends AppCompatActivity {

    private Button Accept;
    private FirebaseDatabase database;
    private String loggedInUser;
    private SharedPreferences pref;
    private String friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        Accept = findViewById(R.id.button);

        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText FriendUsername = findViewById(R.id.editTextNickname);
                friend = FriendUsername.getText().toString().trim();


                database = FirebaseDatabase.getInstance();
                pref = getApplicationContext().getSharedPreferences("Profile", 0);
                loggedInUser = pref.getString("Username", "none");


                //retrieve locations from Firebase and create MyLocationsObject objects
                final DatabaseReference loginRef = database.getReference("users");
                loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean found = false;
                        Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                        for (DataSnapshot user : users) {
                            String usernameP = user.getKey();
                            //loggedInUser
                            if (usernameP.equals(friend)) { //check if this user exists

                                User fullUser = user.getValue(User.class);
                                acceptFriend(fullUser); //friend exists so add
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            Toast.makeText(getApplicationContext(), "Friend Accepted!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Could not accept request", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    private void acceptFriend(final User userToAccept) {
        final DatabaseReference AddFriend = FirebaseDatabase.getInstance().getReference();

        database  = FirebaseDatabase.getInstance();
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        loggedInUser = pref.getString("Username","none");

        //retrieve locations from Firebase and create MyLocationsObject objects
        final DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    //loggedInUser
                    if(usernameP.equals(loggedInUser)){ // Check if this user exists

                        User loginUser = user.getValue(User.class);
                        ArrayList<String> loginUserFriends = loginUser.friends;
                        ArrayList<String> acceptUserFriends = userToAccept.friends;

                        Iterable<DataSnapshot> friendRequests = user.child("friendRequests").getChildren();
                        for (DataSnapshot request:friendRequests) {
                            if (request.getValue().toString().equals(friend)) {
                                // Accept this person's request
                                Toast.makeText(AcceptFriend.this, "Request Accepted", Toast.LENGTH_SHORT).show();

                                // Remove friend request
                                AddFriend.child("users").child(loggedInUser).child("friendRequests").child(userToAccept.username).removeValue();

                                // Remove the false value from the userToAccept db entry
                                AddFriend.child("users").child(userToAccept.username).child("friends").child(loggedInUser + "mySPLITfalse").removeValue();

                                // Add users to each others friend lists
                                AddFriend.child("users").child(userToAccept.username).child("friends").child(String.valueOf(acceptUserFriends.size())).setValue(loggedInUser + "mySPLITtrue");
                                AddFriend.child("users").child(loggedInUser).child("friends").child(String.valueOf(loginUserFriends.size())).setValue(userToAccept.username + "mySPLITtrue");

                                setResult(Activity.RESULT_OK);
                                finish();
                                return;
                            }
                        }

                    }

                }


                ;
            }  @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
