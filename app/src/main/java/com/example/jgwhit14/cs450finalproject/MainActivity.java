package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    private Button signupBtn, loginBtn;
    private EditText loginUsername, loginPW, signUpEmail, signUpName, signUpPW1, signUpPW2, signUpUsername;
    private TextView loginError, pwNoMatch, invalidEmail, pwLenError, emailUsed, userNameTaken;
    private FirebaseDatabase database;
    private String TAG = MainActivity.class.getSimpleName();
    private String loggedInEmail = null;
    private String loggedInUsername = null;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Boolean signUpFail, loggedIn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();
        database  = FirebaseDatabase.getInstance();

        loginBtn = findViewById(R.id.loginBtn);
        loginUsername = findViewById(R.id.loginUsername);
        loginPW = findViewById(R.id.loginPW);

        signupBtn = findViewById(R.id.signUpBtn);


        loginError = findViewById(R.id.loginError);


        //when you click done
        loginPW.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_NULL
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    login();

                    return true;
                } else {
                    return false;
                }
            }
        });


        loginError.setVisibility(View.INVISIBLE);

        // Login
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });


    }

    private void login() {

        loginError.setVisibility(View.INVISIBLE);
        loginUsername.setTextColor(getResources().getColor(R.color.defaultColor));

        //save variables entered to strings
        final String username = loginUsername.getText().toString().trim();
        final String password = loginPW.getText().toString();

        //match the password to
        DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                signUpFail = true;

                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    User loginUser = user.getValue(User.class);

                    if(username.equals(usernameP) && password.equals(loginUser.password)){
                        System.out.println("Login Success");
                        loggedInUsername = username;
                        editor.putString("Username", loggedInUsername).apply();
                        signUpFail = false;
                        loginUsername.setText("");
                        loginPW.setText("");
                    }
                }

                if(!signUpFail){
                    map();
                } else {
                    System.out.println("Login Failed!  User doesn't exists");
                    loginError.setVisibility(View.VISIBLE);
                    loginError.setTextColor(getResources().getColor(R.color.error));
                    loginUsername.setTextColor(getResources().getColor(R.color.error));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void map (){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
