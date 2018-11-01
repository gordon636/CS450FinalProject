package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private Boolean signUpFail = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();
        database  = FirebaseDatabase.getInstance();


        signupBtn = findViewById(R.id.signUpBtn);
        loginBtn = findViewById(R.id.loginBtn);
        loginUsername = findViewById(R.id.loginUsername);
        loginPW = findViewById(R.id.loginPW);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpName = findViewById(R.id.signUpName);
        signUpPW1 = findViewById(R.id.signUpPW1);
        signUpPW2 = findViewById(R.id.signUpPW2);
        signUpUsername = findViewById(R.id.signUpUsername);

        loginError = findViewById(R.id.loginError);
        pwNoMatch = findViewById(R.id.pwNoMatch);
        invalidEmail = findViewById(R.id.invalidEmail);
        pwLenError = findViewById(R.id.pwLenError);
        emailUsed = findViewById(R.id.emailUsed);
        userNameTaken = findViewById(R.id.usernameTaken);


        loginError.setVisibility(View.INVISIBLE);
        pwNoMatch.setVisibility(View.INVISIBLE);
        invalidEmail.setVisibility(View.INVISIBLE);
        pwLenError.setVisibility(View.INVISIBLE);
        emailUsed.setVisibility(View.INVISIBLE);
        userNameTaken.setVisibility(View.INVISIBLE);

        // Sign up
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUpFail = false;

                String name = signUpName.getText().toString();
                final String username = signUpUsername.getText().toString();
                final String email = signUpEmail.getText().toString();
                String password1 = signUpPW1.getText().toString();
                String password2 = signUpPW2.getText().toString();

                // Remove error colors
                signUpEmail.setTextColor(getResources().getColor(R.color.defaultColor));
                signUpPW1.setTextColor(getResources().getColor(R.color.defaultColor));
                signUpPW2.setTextColor(getResources().getColor(R.color.defaultColor));
                signUpUsername.setTextColor(getResources().getColor(R.color.defaultColor));

                // Hide errors
                invalidEmail.setVisibility(View.INVISIBLE);
                emailUsed.setVisibility(View.INVISIBLE);
                pwLenError.setVisibility(View.INVISIBLE);
                pwNoMatch.setVisibility(View.INVISIBLE);
                userNameTaken.setVisibility(View.INVISIBLE);

                // Test to see if username is available
                DatabaseReference usernameRef = database.getReference("users");
                usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> usernames = dataSnapshot.getChildren();
                        for (DataSnapshot aUsername:usernames){
                            if(username.equals(aUsername.getKey())){
                                userNameTaken.setVisibility(View.VISIBLE);
                                userNameTaken.setTextColor(getResources().getColor(R.color.error));
                                signUpUsername.setTextColor(getResources().getColor(R.color.error));
                                signUpFail = true;
                                return;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (signUpFail) {return;}


                // Test for invalid email
                if (!email.contains("@")){
                    invalidEmail.setVisibility(View.VISIBLE);
                    invalidEmail.setTextColor(getResources().getColor(R.color.error));
                    signUpEmail.setTextColor(getResources().getColor(R.color.error));
                    return;
                }

                // Test to see if email is available
                DatabaseReference emailRef = database.getReference("users");
                emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> emails = dataSnapshot.getChildren();
                        for (DataSnapshot aEmail:emails){
                            User user = aEmail.getValue(User.class);
                            System.out.println(user.email + " " + email);
                            if(email.equals(user.email)){
                                emailUsed.setVisibility(View.VISIBLE);
                                emailUsed.setTextColor(getResources().getColor(R.color.error));
                                signUpEmail.setTextColor(getResources().getColor(R.color.error));
                                signUpFail = true;
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(signUpFail){return;}


                // Passwords aren't equal --> don't allow login
                if(!password1.equals(password2)){
                    pwNoMatch.setTextColor(getResources().getColor(R.color.error));
                    pwNoMatch.setVisibility(View.VISIBLE);
                    signUpPW2.setTextColor(getResources().getColor(R.color.error));
                    return;
                }

                if(password1.length() < 4){
                    pwLenError.setVisibility(View.VISIBLE);
                    pwLenError.setTextColor(getResources().getColor(R.color.error));
                    signUpPW1.setTextColor(getResources().getColor(R.color.error));
                    signUpPW2.setTextColor(getResources().getColor(R.color.error));
                    return;
                }

                User user = new User(email, password1, name, username);
                user.writeNewUser(user);

                System.out.println("Sign-up Successful");
                // If code reaches here, username not taken
                loggedInUsername = username;
                editor.putString("Username", loggedInUsername).apply();
                return;

            }
        });


        // Login
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signUpFail = false;

                loginError.setVisibility(View.INVISIBLE);
                loginUsername.setTextColor(getResources().getColor(R.color.defaultColor));

                //save variables entered to strings
                final String username = loginUsername.getText().toString();
                final String password = loginPW.getText().toString();

                //match the password to
                DatabaseReference loginRef = database.getReference("users");
                loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> users = dataSnapshot.getChildren();

                        for (DataSnapshot user:users){

                            User loginUser = user.getValue(User.class);
                            Log.i(TAG, loginUser.username);

                            if(username.equals(loginUser.username) && password.equals(loginUser.password)){
                                System.out.println("Login Success");
                                loggedInUsername = username;
                                editor.putString("Username", loggedInUsername).apply();
                                return;
                            }
                        }

                        System.out.println("Login Failed!  User doesn't exists");
                        loginError.setVisibility(View.VISIBLE);
                        loginError.setTextColor(getResources().getColor(R.color.error));
                        loginUsername.setTextColor(getResources().getColor(R.color.error));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        return;

    }

    public void map (View view){

        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }
}
