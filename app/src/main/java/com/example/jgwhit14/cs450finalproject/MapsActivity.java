package com.example.jgwhit14.cs450finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

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
    private LatLng myLocation;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private final static int PERMISSION_REQUEST_CODE = 999;
    private static final int REQUEST_LOCATION = 1;
    private final static String LOGTAG = MapsActivity.class.getSimpleName();
    private ArrayList locationsList;
    private FirebaseDatabase database;
    private String loggedInUser;

    private GoogleMap mMap;

    private  Marker MyLocationMarker;
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
        pref = getApplicationContext().getSharedPreferences("Profile",0);

        TextView title = findViewById(R.id.textViewTitle);
        title.setText("Where You At - "+pref.getString("Username","none"));
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
                    String realLocation = "Unknown Location";
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> listAddresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        if(null!=listAddresses&&listAddresses.size()>0){
                            realLocation = listAddresses.get(0).getAddressLine(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String holder = realLocation+"\n- "+currentLocation.getLatitude() + ", "+currentLocation.getLongitude();

                    textViewLocation.setText(holder);

                //myButton.setEnabled(true);

                    // Add a marker to your location and move the camera
                     myLocation = new LatLng(lat, lon);

                   //mMap.clear();
                    MarkerOptions myLocationMarkerOptions = new MarkerOptions().position(myLocation).title("You are Here");

                    if (MyLocationMarker == null){
                        MyLocationMarker =mMap.addMarker(myLocationMarkerOptions);
                    }else{
                        MyLocationMarker.remove();
                        MyLocationMarker =mMap.addMarker(myLocationMarkerOptions);
                    }


                    if(!loaded) {

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                        loadPointers();
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

        Toast.makeText(getApplicationContext(),"Waiting for gps, please wait!",Toast.LENGTH_LONG).show();

        if(loaded) {

            String lon = String.valueOf(currentLocation.getLatitude());
            String lat = String.valueOf(currentLocation.getLongitude());
            //save current location
            Toast.makeText(getApplicationContext(),"Location Saved!",Toast.LENGTH_LONG).show();
            DatabaseReference AddLocation = FirebaseDatabase.getInstance().getReference();

//

            // AddLocation.child("users").child("d").child("locations").child(myList.get(myList.indexOf(currentLocation)).toString()).setValue(lat + ", " + lon);


            Intent intent = new Intent(this,SaveLocation.class);
            intent.putExtra("location",currentLocation);
            intent.putExtra("Username",pref.getString("Username","none"));
            //startActivity(intent);
            startActivityForResult(intent,1234);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if (resultCode == Activity.RESULT_OK){
                loadPointers();
            }
         }

    public void myRefreshLocation (View view){

        if(loaded) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18),2000, null);
        }
    }

    public void loadPointers (){


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
                        ArrayList<String> userLocations = loginUser.locations;
                        System.out.println("LOCATIONS: " + userLocations);


                        if (userLocations !=null){






                        for(String aLocation:userLocations) {
                            if (aLocation == null) {
                                continue;
                            }
                            String[] aLocationArr = aLocation.split("mySPLIT");

                            Location location = new Location("");
                            location.setLatitude(Double.parseDouble(aLocationArr[0]));
                            location.setLongitude(Double.parseDouble(aLocationArr[1]));

                            myList.add(location);

                            MyLocationsObject locationToList = new MyLocationsObject(loggedInUser, location, aLocationArr[5], aLocationArr[6], aLocationArr[2]);

//                            locationsList.add(locationToList);

                            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            final Marker marker = mMap.addMarker(new MarkerOptions().position(myLocation).title(aLocationArr[2]).icon(BitmapDescriptorFactory.defaultMarker(randomColor())));

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker aMarker) {

                                    if (aMarker == marker) {
                                        Toast.makeText(getApplicationContext(), "Marker Clicked", Toast.LENGTH_LONG).show();
                                    }
                                    return false;
                                }
                            });

                            System.out.println("Placed location");
                        }
                        }


                        break;
                    }



                }

                ;
            }  @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private float randomColor() {

        Random random = new Random();

        int myChoice = random.nextInt(8);

        switch (myChoice){

            case 0: return BitmapDescriptorFactory.HUE_ORANGE;
            case 1: return BitmapDescriptorFactory.HUE_AZURE;
            case 2: return BitmapDescriptorFactory.HUE_CYAN;
            case 3: return BitmapDescriptorFactory.HUE_MAGENTA;
            case 4: return BitmapDescriptorFactory.HUE_GREEN;
            case 5: return BitmapDescriptorFactory.HUE_ROSE;
            case 6: return BitmapDescriptorFactory.HUE_VIOLET;
            case 7: return BitmapDescriptorFactory.HUE_YELLOW;

            default: return BitmapDescriptorFactory.HUE_ORANGE;
        }


    }
}

