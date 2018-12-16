package com.example.jgwhit14.cs450finalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectLocation extends AppCompatActivity {
    private ArrayList<Location> myList;
    private Button Save;
    private Button Cancel;
    private FirebaseDatabase database;
    private String loggedInUser;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String realLocation;
    private Location currentLocation;
    private TextView nickname,note;
    private Bitmap bitmap;
    private String REMOTE_SERVER = "http://tablemate.online/whereyouat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();


        Save = findViewById(R.id.button3);
        Cancel = findViewById(R.id.button);

        if (pref.getString("friend","false").equals("true")){

            Button button = findViewById(R.id.button3);
            button.setVisibility(View.GONE);
        }

        final Intent i = this.getIntent();
         currentLocation = i.getExtras().getParcelable("location");
//        System.out.println("array  "+ myList.toString());

//        myList.add(currentLocation);
        realLocation = "Unknown Location";
        int id = i.getIntExtra("id", 0);
        editor.putString("idSel",String.valueOf(id)).apply();
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

         nickname = findViewById(R.id.textViewNickname);
        note = findViewById(R.id.textViewNote);
        downloadData();

        String idImage = pref.getString("idSel","0");
        System.out.println("We have selected image: "+id);
        String Data = pref.getString("Username", "...");
        new LoadImage().execute(REMOTE_SERVER + "/" + Data + "/"+idImage+".jpg");

    }

    public void share (View view){

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_TEXT, "Hey check this place out: \n\n" +
                "Title: "+nickname.getText()+
                "\nAddress: "+realLocation+
                "\nCoordinates: "+currentLocation.getLatitude()+", "+currentLocation.getLongitude()+
                "\n\nView On Google Maps - https://maps.google.com/?ll="+currentLocation.getLatitude()+","+currentLocation.getLongitude()
                        +"\n\nHope you find this useful!" +
                "\n\n - "+loggedInUser);

        share.putExtra(Intent.EXTRA_SUBJECT, "Location From WHERE YOU AT! App ("+loggedInUser+")");

        startActivity(Intent.createChooser(share, "Share Your Location"));
    }

    private void downloadData() {

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


                        int index = 0;
                        for(String aLocation:userLocations){
                            if(aLocation == null){
                                continue;
                            }
                            String[] aLocationArr = aLocation.split("mySPLIT");

                            Location location = new Location("");
                            location.setLatitude(Double.parseDouble(aLocationArr[0]));
                            location.setLongitude(Double.parseDouble(aLocationArr[1]));

                            if (currentLocation.getLatitude() == location.getLatitude() & currentLocation.getLongitude() == location.getLongitude()){

                                // This is the data for the selected location... set text fields
                                nickname.setText("Nickname: "+aLocationArr[2]);
                                note.setText("Notes: "+aLocationArr[4]);

                                System.out.println("Index of this place is: "+index);
                                editor.putString("selectedLocationIndex", String.valueOf(index)).apply();
                            }

                            index ++;
                        }


                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK){
            downloadData();
        }
    }
    public void edit (View view){

        Intent intent = new Intent(this, EditLocation.class);
        intent.putExtra("location",currentLocation);
        intent.putExtra("nickname",nickname.getText().toString());
        intent.putExtra("note",note.getText().toString());
        intent.putExtra("Username",loggedInUser);
        startActivityForResult(intent,1234);
    }
    public void cancel (View view){

        finish();
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                SharedPreferences images = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = images.edit();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;


        }


        protected void onPostExecute(Bitmap image1) {

            if (image1 != null) {
                ImageView image = (ImageView)findViewById(R.id.imageViewPrev);



                image.setImageBitmap(bitmap);




            } else {


            }
        }


    }

}
