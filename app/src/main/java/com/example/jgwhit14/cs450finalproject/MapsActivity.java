package com.example.jgwhit14.cs450finalproject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, Observer, NavigationView.OnNavigationItemSelectedListener{

    //GPS
    private ListView listView;
    private ArrayList<Location> myList;
    private HashMap<String, ArrayList<Marker>> myFriendDisplayLocationList;
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
    private Circle circle;
    private final static int PERMISSION_REQUEST_CODE = 999;
    private static final int REQUEST_LOCATION = 1;
    private final static String LOGTAG = MapsActivity.class.getSimpleName();
    private ArrayList locationsList;
    private FirebaseDatabase database;
    private String loggedInUser;

    private GoogleMap mMap;
    private ArrayList friendsList;
    private  Marker MyLocationMarker;
    private  ArrayList recommendedLocations;
    //counter
    private Timer t = null;
    private Counter ctr = null;
    int lastMinute;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myList = new ArrayList<>();
        textViewLocation = findViewById(R.id.textViewLocation);
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();

//
        TextView title = findViewById(R.id.textViewTitle);
        title.setText("Where You At! - "+pref.getString("Username","none"));
        //initiate the handler
        if (handler == null) {
            this.handler = new LocationHandler(this);
            this.handler.addObserver(this);
        }

         dialog = ProgressDialog.show(MapsActivity.this, "",
                "Loading. Please wait...", true);

        myFriendDisplayLocationList = new HashMap<>();
        // check permissions
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_REQUEST_CODE
            );
        }


        //buttons


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onMapReady(mMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            onMapReady(mMap);
            int currentRadius = pref.getInt("locationRadius",30);

            if (loaded) {

                if (circle !=null){
                    circle.remove();
                }
                getFriends();
                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                        .radius(currentRadius * 1000)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(50, 0, 0, 255)));

                if (pref.getString("radiusOn", "true").equals("false")){
                    circle.setVisible(false);
                }else{
                    circle.setVisible(true);
                }
            }

        }
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
        mMap = googleMap;



        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            int style = pref.getInt("MAP_STYLE", 1);
            boolean success = false;
            if(style == 1){
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));
            }else if(style == 2){
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style2_json));
            }else{
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style3_json));
            }



            if (!success) {
                Log.e(LOGTAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(LOGTAG, "Can't find style. Error: ", e);
        }

        //load friend location here.
        String friend = pref.getString("FRIEND_LOCATION_SHOW", "");
        System.out.println("MY FREIND IS:" + friend);
        if(!friend.equals("")){
            if(myFriendDisplayLocationList.containsKey(friend)){
                deletePointers(friend);
                myFriendDisplayLocationList.remove(friend);
            }else{
                myFriendDisplayLocationList.put(friend, new ArrayList<Marker>());

                if (loaded && mMap !=null) {
                    loadPointers(friend);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                }
            }
            //reset
            editor.putString("FRIEND_LOCATION_SHOW", "").apply();
        }


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
                        loggedInUser = pref.getString("Username","none");
                        loadPointers(loggedInUser);
                        dialog.dismiss();

                        int currentRadius = pref.getInt("locationRadius",30);


                        if (circle !=null){
                            circle.remove();
                        }
                            circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                    .radius(currentRadius * 1000)
                                    .strokeColor(Color.RED)
                                    .fillColor(Color.argb(50, 0, 0, 255)));
                            if (pref.getString("radiusOn", "true").equals("false")){
                                circle.setVisible(false);
                            }else{
                                circle.setVisible(true);
                            }

                    }
                    loaded=true;


                }
            });
        }
    }

    public void locations (View view){

        locations();
    }
    public void locations (){

        Intent intent = new Intent(this, MyLocations.class);
        startActivity(intent);
    }

    public void friends (){

        Intent intent = new Intent(this, Friends.class);
        startActivity(intent);
    }
    public void friends (View view){

       friends();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (t !=null) {
            t.cancel();
            ctr.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (t !=null) {
            t.cancel();
            ctr.cancel();
        }
    }

    public void save (View view){
        save();
    }

    public void save(){
        Toast.makeText(getApplicationContext(),"Waiting for gps, please wait!",Toast.LENGTH_LONG).show();

        if(loaded) {

            String lon = String.valueOf(currentLocation.getLatitude());
            String lat = String.valueOf(currentLocation.getLongitude());
            //save current location
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
                loggedInUser = pref.getString("Username","none");

                if (mMap!=null && loaded) {
                    loadPointers(loggedInUser);
                }
              }
         }

    public void myRefreshLocation (View view){

        if(loaded) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18),2000, null);
        }
    }

    public void loadPointers (final String anuser){



            if(mMap!=null && loaded){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));


            }
        //    mMap.animateCamera(CameraUpdateFactory.zoomTo(18),2000, null);
        int currentRadius = pref.getInt("locationRadius",30);


        if (loaded) {

            if (circle !=null){
                circle.remove();
            }
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .radius(currentRadius * 1000)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(50, 0, 0, 255)));
            if (pref.getString("radiusOn", "true").equals("false")){
                circle.setVisible(false);
            }else{
                circle.setVisible(true);
            }
        }


        ctr = new Counter();
        ctr.count = 0;
        t = new Timer();

        t.scheduleAtFixedRate(ctr, 0, 100); //tenth of sec

        database  = FirebaseDatabase.getInstance();
        pref = getApplicationContext().getSharedPreferences("Profile",0);

        //retrieve locations from Firebase and create MyLocationsObject objects
        DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    //loggedInUser
                    if(usernameP.equals(anuser)){
                        User loginUser = user.getValue(User.class);
                        ArrayList<String> userLocations = loginUser.locations;
                        System.out.println("LOCATIONS: " + userLocations);


                        if (userLocations !=null){



                        //pick a random color
                        float c = randomColor();
                        if(anuser.equals(loggedInUser)){
                            c = BitmapDescriptorFactory.HUE_BLUE;
                        }

                        int id = 0;
                        for(String aLocation:userLocations) {
                            if (aLocation == null) {
                                continue;
                            }
                            String[] aLocationArr = aLocation.split("mySPLIT");

                            final Location location = new Location("");
                            location.setLatitude(Double.parseDouble(aLocationArr[0]));
                            location.setLongitude(Double.parseDouble(aLocationArr[1]));

                            if(anuser.equals(loggedInUser)){
                                myList.add(location);
                            }

                            MyLocationsObject locationToList = new MyLocationsObject(anuser, location, aLocationArr[5], aLocationArr[6], aLocationArr[2], aLocationArr[3]);


//                            locationsList.add(locationToList);

                            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            final Marker marker = mMap.addMarker(new MarkerOptions().position(myLocation).title(aLocationArr[2]).icon(BitmapDescriptorFactory.defaultMarker(c)));


                            marker.setTag(id);


                            if(!anuser.equals(loggedInUser)){
                                myFriendDisplayLocationList.get(anuser).add(marker);
                            }

                            final int finalId = id;
                            id++;
                            if (marker.getTag() == null){
                                marker.setZIndex(500);
                            }
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker aMarker) {

                                    if (aMarker.getTag() == null){
                                        aMarker.setZIndex(500);
                                    }else {
                                        Intent intent = new Intent(MapsActivity.this, SelectLocation.class);
                                        intent.putExtra("location", location);
                                        intent.putExtra("bundle", aMarker.getPosition());
                                        intent.putExtra("Username", loggedInUser);
                                        intent.putExtra("id", aMarker.getTag().toString() + "mySPLIT" + anuser);

                                        startActivity(intent);

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

    public void deletePointers(String user){
        ArrayList<Marker> ar = myFriendDisplayLocationList.get(user);

        for(Marker k: ar){
            k.remove();
        }
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
        else if (id == R.id.nav_save) {
            save();
        }else if (id == R.id.nav_share) {

            //click on my location
            pref.edit().putString("friend","false").apply();

            Toast.makeText(getApplicationContext(),"Select a location to share!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MyLocations.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            Setting();

        }else if (id == R.id.nav_recommend) {

            if (!loaded||recommendedLocations.size() == 0){
                Toast.makeText(getApplicationContext(), "No Recommendations Available!", Toast.LENGTH_LONG).show();
                TextView recommend = findViewById(R.id.textViewRecomend);
                recommend.setText("0");


            }else {
                Intent intent = new Intent(this, ViewRecommendations.class);
                intent.putExtra("Locations",recommendedLocations);
                startActivity(intent);
            }
        }
            else
         if (id == R.id.nav_logout) {

                finish();
                //
                editor.putString("Username", "").apply();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


    public HashMap<String,String> getFriends (){



        //get friends from Firebase
      //  friendsList.clear();
        database  = FirebaseDatabase.getInstance();
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        loggedInUser = pref.getString("Username","none");

        //retrieve locations from Firebase and create MyLocationsObject objects
        DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> userLocations = new ArrayList<>();
                locationsList = new ArrayList<>();
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    //loggedInUser
                    User loginUser = user.getValue(User.class);

                    System.out.println("CHECKING "+usernameP + " locations: "+loginUser.locations.size() );


                     if (loginUser.locations.size() !=0) {
                         for (int i = 0; i < loginUser.locations.size(); i++) {

                             userLocations.add(usernameP+"myFriendSPLIT"+loginUser.locations.get(i));
                             System.out.println(usernameP+"myFriendSPLIT"+loginUser.locations.get(i));
                         }
                     }

                  //   if(usernameP.equals(loggedInUser)){

                        ArrayList<String> userFirends = loginUser.friends;

                        System.out.println("Friends: " + userFirends);


                        //check if i have friends added
                        if (userFirends != null){

                            //has friends, check friend to see if they added us back if so add to my friends

                            for(String aFirend:userFirends){
                                if(aFirend == null){
                                    continue;
                                }

                                System.out.println("currentFriend: "+aFirend);

                                String[] currentFriendData = aFirend.split("mySPLIT");
                                System.out.println("currentFriendData: "+currentFriendData.length);

                                //go through userLocations list and filter out friends
                                for (int i =0; i <userLocations.size();i++){

                                    System.out.println("LOCATION: "+userLocations.get(i));
                                }

                                for (String aLocation: userLocations) { //go through all the locations

                                    String[] getUser = aLocation.split("myFriendSPLIT");
                                    String username = getUser[0];

                                    System.out.println("User to check: "+username + " logged in "+loggedInUser);

                                    if (!username.equals(loggedInUser)) { //dont check logged in users locations
                                        System.out.println("current username: " + username + " to match with: "+currentFriendData[0]);


                                       // if (username.equals(currentFriendData[0])) {
                                            System.out.println("friend username: " + currentFriendData[0]);
                                            if(!locationsList.contains(aLocation) ){
                                                locationsList.add(aLocation);
                                            }
                                        //}

                                    }
                                }


                            }

                        }


                   // }

                    //System.out.println("OUR LOCATIONS: "+locationsList);
                }
                recommended(currentLocation,pref.getInt("locationRadius",30)); //radius needs to be calculated settings


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return null;
    }

    public void recommended (Location myLocation, double radius){

        System.out.println("MY LOCATION: "+myLocation.getLatitude()+" lon: "+myLocation.getLongitude());
         recommendedLocations = new ArrayList<>();
        //looop through friends locations, check the distance from your current location, if it is within th specified radius, recoomned to the user

        for (Object friendLocation: locationsList){

            System.out.println("Check: "+friendLocation);

            String[] userData =   friendLocation.toString().split("myFriendSPLIT");

            String []coordinates = userData[1].split("mySPLIT");



            //now compare lat lon to current location
            Location myFriendLocation= new Location("");
            myFriendLocation.setLongitude(Double.valueOf(coordinates[1]));
            myFriendLocation.setLatitude(Double.valueOf(coordinates[0]));

            double distance = myLocation.distanceTo(myFriendLocation);//disstance betwwen locations in meters

            if (distance < radius *1000){ //use km radius and distance
                System.out.println("Distance: "+distance);

                recommendedLocations.add(friendLocation);
            }


        }

        if (recommendedLocations.size() > 0) {
            int size = recommendedLocations.size();
            int currentRadius = pref.getInt("locationRadius",30);

            Toast.makeText(getApplicationContext(), "You have " + size + " recommended location(s) within "+currentRadius+"km!" , Toast.LENGTH_LONG).show();
            TextView recommend = findViewById(R.id.textViewRecomend);
            recommend.setText(String.valueOf(size)+" *");
        }else{
            TextView recommend = findViewById(R.id.textViewRecomend);
            recommend.setText("0");
        }
    }

    public void recommend (View view){

        getFriends();
    }



    class Counter extends TimerTask {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);


        private int count =   0;
        private  int interval = 5;
        @Override
        public void run() {


            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //calculate min secs and split sec from 10th of sec
                    int min = count /600;

//                    System.out.println("min - lastMinute: "+(min - lastMinute));
                    if ((min - lastMinute) ==interval && lastMinute != min || count ==0){ //must recommend locations every 5mins or when the timer starts

                        System.out.println("counting - " + lastMinute);
                        getFriends();
                        lastMinute = min;
                    }

                    count++;
                }
            });
        }
    }

    public void recommended (View view){

        recommended();
    }

    private void recommended() {

        if (!loaded||recommendedLocations.size() == 0){
            Toast.makeText(getApplicationContext(), "No Recommendations Available!", Toast.LENGTH_LONG).show();
            TextView recommend = findViewById(R.id.textViewRecomend);
            recommend.setText("0");

        }else {
            Intent intent = new Intent(this, ViewRecommendations.class);
            intent.putExtra("Locations",recommendedLocations);
            startActivity(intent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            finish();
            Intent intent = new Intent(MapsActivity.this,MapsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

public void refresh (View view){

        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
        finish();
}
}

