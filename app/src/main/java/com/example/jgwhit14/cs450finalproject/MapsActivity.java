package com.example.jgwhit14.cs450finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Observer {

    //GPS
    private ListView listView;
    private ArrayList<Location> myList;
    public double currentDistance = 0, totalDistance = 0;
    public Button myButton,resetButton;
    private Location startLocation = null;
    private Location currentLocation = null;
    private Location prev_Location = null;
    private LocationHandler handler = null;
    private double overalVelocity;
    private double pointVelocity;
    public double instantVel;
    private boolean permissions_granted;
    private boolean isRestart = false;
    private String myData;
    private Toast ToastMess;
    private DecimalFormat df = new DecimalFormat("0.00");
    private TextView textViewLocation;
    private boolean loaded = false;

    private final static int PERMISSION_REQUEST_CODE = 999;
    private static final int REQUEST_LOCATION = 1;
    private final static String LOGTAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myList = new ArrayList<>();
        textViewLocation = findViewById(R.id.textViewLocation);

        //initiate the handler
        if (handler == null) {
            this.handler = new LocationHandler(this);
            this.handler.addObserver(this);
        }

        // check permissions
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_REQUEST_CODE
            );
        }

        //buttons
    }

    //permission granted or not
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // we have only asked for FINE LOCATION
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.permissions_granted = true;
                Log.i(LOGTAG, "Fine location permission granted.");
            }
            else {
                this.permissions_granted = false;
                Log.i(LOGTAG, "Fine location permission not granted.");
            }
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(LOGTAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(LOGTAG, "Can't find style. Error: ", e);
        }


        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof LocationHandler) {
            final Location l = (Location) o;
            final double lat = l.getLatitude();
            final double lon = l.getLongitude();
            final long time = l.getTime();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(MapsActivity.this.startLocation == null || isRestart){
                        //   ToastMess = Toast.makeText(getApplicationContext(),"Ready to record",Toast.LENGTH_SHORT);
                        //   ToastMess.show();
                        isRestart = false;
                    }

                    MapsActivity.this.currentLocation = new Location("");
                    MapsActivity.this.currentLocation.setLatitude(lat);
                    MapsActivity.this.currentLocation.setLongitude(lon);
                    MapsActivity.this.currentLocation.setTime(time);

                 //   String holder = "Your Current Location: "+currentLocation.getLatitude() + ", "+currentLocation.getLongitude()+
                   //         "\nInstantaneous Velocity: "+df.format(Double.valueOf(instantVel))+ " m/s" + "\nOverall Velocity: "+df.format(Double.valueOf(overalVelocity))+ " m/s";

                    String holder = "Your Current Location: "+currentLocation.getLatitude() + ", "+currentLocation.getLongitude();

                    textViewLocation.setText(holder);

                //myButton.setEnabled(true);

                    // Add a marker to your location and move the camera
                    LatLng myLocation = new LatLng(lat, lon);

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(myLocation).title("You are Here"));

                    if(!loaded) {

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    }
                    loaded=true;


                }
            });
        }
    }

    public void locations (View view){

        Intent intent = new Intent(this, MyLocations.class);
        startActivity(intent);
    }

    public void friends (View view){

        Intent intent = new Intent(this, Friends.class);
        startActivity(intent);
    }


    public void save (View view){

        String lon = String.valueOf(currentLocation.getLatitude());
        String lat = String.valueOf(currentLocation.getLongitude());
        //save current location
        Toast.makeText(getApplicationContext(),"Location Saved!",Toast.LENGTH_LONG).show();
        DatabaseReference AddLocation = FirebaseDatabase.getInstance().getReference();

        myList.add(currentLocation);

       // AddLocation.child("users").child("d").child("locations").child(myList.get(myList.indexOf(currentLocation)).toString()).setValue(lat + ", " + lon);


        Intent intent = new Intent(this,SaveLocation.class);
        startActivity(intent);
    }
}
