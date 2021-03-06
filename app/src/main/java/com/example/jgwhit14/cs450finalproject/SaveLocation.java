package com.example.jgwhit14.cs450finalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SaveLocation extends AppCompatActivity {
    private ArrayList<Location> myList;
    private Button Save;
    private Button Cancel;
    private FirebaseDatabase database;
    private String loggedInUser;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String realLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);

        Save = findViewById(R.id.button3);
        Cancel = findViewById(R.id.button);
        final Intent i = this.getIntent();
        final Location currentLocation = i.getExtras().getParcelable("location");
//        System.out.println("array  "+ myList.toString());

//        myList.add(currentLocation);
         realLocation = "Unknown Location";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                realLocation = listAddresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView RealLocation = findViewById(R.id.textViewRealName);
        RealLocation.setText(realLocation);

        TextView coordinates = findViewById(R.id.textViewCoordinates);
        coordinates.setText(currentLocation.getLatitude()+ ","+currentLocation.getLongitude());


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference AddLocation = FirebaseDatabase.getInstance().getReference();

                EditText Nickname = findViewById(R.id.editTextNickname);
                EditText Note = findViewById(R.id.editTextNote);

                final String username = i.getExtras().getString("Username");
                final String nickname = Nickname.getText().toString();
                final String realName = realLocation;
                final String note = Note.getText().toString();

                //Repeater
                Calendar c = Calendar.getInstance();
                System.out.println("Current time =&gt; " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
                final String formattedDate = df.format(c.getTime());
                SimpleDateFormat time = new SimpleDateFormat("hh:mm c");
                final String formattedTime = time.format(c.getTime());


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
                            if(usernameP.equals(loggedInUser)){
                                User loginUser = user.getValue(User.class);
                                ArrayList<String> userLocations = loginUser.locations;
                                System.out.println("LOCATIONS: " + userLocations);

                                if (userLocations != null) {
                                    AddLocation.child("users").child(username).child("locations").child(String.valueOf(userLocations.size())).setValue(currentLocation.getLatitude() + "mySPLIT" + currentLocation.getLongitude() + "mySPLIT" + nickname + "mySPLIT" + realName + "mySPLIT" + note + "mySPLIT" + formattedDate + "mySPLIT" + formattedTime);
                                }else{
                                    AddLocation.child("users").child(username).child("locations").child(String.valueOf(0)).setValue(currentLocation.getLatitude() + "mySPLIT" + currentLocation.getLongitude() + "mySPLIT" + nickname + "mySPLIT" + realName + "mySPLIT" + note + "mySPLIT" + formattedDate + "mySPLIT" + formattedTime);
                                }

                            }



                        }

                        ;
                    }  @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //"44.5878623)(-75.1610814)(wazzza)(realName)(john )(15 Nov 2018)(11:15"
                Toast.makeText(getApplicationContext(),"Location Saved!",Toast.LENGTH_LONG).show();

                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                setResult(0);
            }
        });
    }

    private int countList() {



        return 0;
    }

}

