package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Register extends AppCompatActivity {

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
        setContentView(R.layout.activity_register);

        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();
        database  = FirebaseDatabase.getInstance();

        signupBtn = findViewById(R.id.signUpBtn);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpName = findViewById(R.id.signUpName);
        signUpPW1 = findViewById(R.id.signUpPW);
        signUpPW2 = findViewById(R.id.signUpPW2);
        signUpUsername = findViewById(R.id.signUpUsername);

        emailUsed = findViewById(R.id.emailUsed);
        userNameTaken = findViewById(R.id.usernameTaken);
        pwNoMatch = findViewById(R.id.pwNoMatch);
        invalidEmail = findViewById(R.id.invalidEmail);
        pwLenError = findViewById(R.id.pwLenError);

        pwNoMatch.setVisibility(View.INVISIBLE);
        invalidEmail.setVisibility(View.INVISIBLE);
        pwLenError.setVisibility(View.INVISIBLE);
        emailUsed.setVisibility(View.INVISIBLE);
        userNameTaken.setVisibility(View.INVISIBLE);

        // Sign up
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            register();

            }
        });

        //when you click done
        signUpPW2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_NULL
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    register();

                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    private void register() {

        signUpFail = false;

        final String name = signUpName.getText().toString().trim();
        final String username = signUpUsername.getText().toString().trim();
        final String email = signUpEmail.getText().toString().trim();
        final String password1 = signUpPW1.getText().toString();
        final String password2 = signUpPW2.getText().toString();

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
                        Log.i(TAG, aUsername.getKey());
                        userNameTaken.setVisibility(View.VISIBLE);
                        userNameTaken.setTextColor(getResources().getColor(R.color.error));
                        signUpUsername.setTextColor(getResources().getColor(R.color.error));
                        signUpFail = true;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Test for invalid email

        if (!email.contains("@")){
            invalidEmail.setVisibility(View.VISIBLE);
            invalidEmail.setTextColor(getResources().getColor(R.color.error));
            signUpEmail.setTextColor(getResources().getColor(R.color.error));
            signUpFail = true;
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
                    }
                }

                // Passwords aren't equal --> don't allow login
                if(!password1.equals(password2)){
                    pwNoMatch.setTextColor(getResources().getColor(R.color.error));
                    pwNoMatch.setVisibility(View.VISIBLE);
                    signUpPW2.setTextColor(getResources().getColor(R.color.error));
                    signUpFail = true;
                }

                if(password1.length() < 4){
                    pwLenError.setVisibility(View.VISIBLE);
                    pwLenError.setTextColor(getResources().getColor(R.color.error));
                    signUpPW1.setTextColor(getResources().getColor(R.color.error));
                    signUpPW2.setTextColor(getResources().getColor(R.color.error));
                    signUpFail = true;
                }
                if(!signUpFail) {
                    User user = new User(email, password1, name, username);
                    user.writeNewUser(user);

                    System.out.println("Sign-up Successful");
                    // If code reaches here, username not taken
                    loggedInUsername = username;
                    editor.putString("Username", loggedInUsername).apply();

                    map();
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
