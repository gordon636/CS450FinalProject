package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SaveLocation extends AppCompatActivity {
    private ArrayList<Location> myList;
    private Button Save;
    private Button Cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);

        Save = findViewById(R.id.button3);
        Cancel = findViewById(R.id.button);
        final Intent i = this.getIntent();
        final Location currentLocation = i.getExtras().getParcelable("location");
        myList = i.getParcelableArrayListExtra("list");
        myList.add(currentLocation);



        TextView coordinates = findViewById(R.id.textViewCoordinates);
        coordinates.setText(currentLocation.getLatitude()+ ","+currentLocation.getLongitude()
        );

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference AddLocation = FirebaseDatabase.getInstance().getReference();

                EditText Nickname = findViewById(R.id.editTextNickname);
                EditText Note = findViewById(R.id.editTextNote);

                String username = i.getExtras().getString("Username");
                String nickname = Nickname.getText().toString();
                String realName = "realName";
                String note = Note.getText().toString();

                //Repeater
                Calendar c = Calendar.getInstance();
                System.out.println("Current time =&gt; " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
                String formattedDate = df.format(c.getTime());
                SimpleDateFormat time = new SimpleDateFormat("hh:mm");
                String formattedTime = time.format(c.getTime());




                AddLocation.child("users").child(username).child("locations").child(Integer.toString(
                        (myList.indexOf(currentLocation)))).setValue(currentLocation.getLatitude() + "mySPLIT" + currentLocation.getLongitude()+"mySPLIT"+nickname+"mySPLIT"+realName+"mySPLIT"+note+"mySPLIT"+formattedDate+"mySPLIT"+formattedTime);
                finish();
                //"44.5878623)(-75.1610814)(wazzza)(realName)(john )(15 Nov 2018)(11:15"
                Toast.makeText(getApplicationContext(),"Location Saved!",Toast.LENGTH_LONG).show();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}

