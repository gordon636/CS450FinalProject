package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    private Button signupBtn, loginBtn;
    private EditText loginEmail, loginPW, signUpEmail, signUpName, signUpPW1, signUpPW2;
    private FirebaseDatabase database;
    private String TAG = MainActivity.class.getSimpleName();
    private Boolean loginFound = false;
    private String loggedInEmail = null;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();
        database  = FirebaseDatabase.getInstance();


        signupBtn = findViewById(R.id.signUpBtn);
        loginBtn = findViewById(R.id.loginBtn);
        loginEmail = findViewById(R.id.loginEmail);
        loginPW = findViewById(R.id.loginPW);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpName = findViewById(R.id.signUpName);
        signUpPW1 = findViewById(R.id.signUpPW1);
        signUpPW2 = findViewById(R.id.signUpPW2);



        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signUpName.getText().toString();
                String email = signUpEmail.getText().toString();
                String password1 = signUpPW1.getText().toString();
                String password2 = signUpPW2.getText().toString();

                // Test for invalid email

                if (!email.contains("@")){
                    signUpEmail.setText("");
                    signUpEmail.setHint(R.string.invalidEmail);
                    signUpEmail.setHintTextColor(getResources().getColor(R.color.error));
                    return;
                }
                // Test to see if email is available
                DatabaseReference myRef = database.getReference("users");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot);
                        Iterable<DataSnapshot> emails = dataSnapshot.getChildren();
                        System.out.println(emails);
                        for (DataSnapshot email:emails){
                            User user = email.getValue(User.class);
                            System.out.println(user.email + " " + email);
                            if(email.equals(user.email)){
                                signUpEmail.setText("");
                                signUpEmail.setHint(R.string.emailUsedError);
                                signUpEmail.setHintTextColor(getResources().getColor(R.color.error));
                                loggedInEmail = email.toString();
                                editor.putString("Email",loggedInEmail).apply();


                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                // Passwords aren't equal --> don't allow login
                if(!password1.equals(password2)){
                    signUpPW2.setText("");
                    signUpPW2.setHint(R.string.passwordNoMatch);
                    signUpPW2.setHintTextColor(getResources().getColor(R.color.error));
                    return;
                }

                if(password1.length() < 4){
                    signUpPW1.setText("");
                    signUpPW1.setHint(R.string.passwordLenError);
                    signUpPW1.setHintTextColor(getResources().getColor(R.color.error));
                    signUpPW2.setText("");
                    signUpPW2.setHint(R.string.passwordLenError);
                    signUpPW2.setHintTextColor(getResources().getColor(R.color.error));
                    return;
                }

                User user = new User(email, password1, name);
                user.writeNewUser(user);

            }
        });



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFound = false;

                //save variables entered to strings
                final String email = loginEmail.getText().toString();
                final String password = loginPW.getText().toString();

                //match the password to
                DatabaseReference loginRef = database.getReference("users");
                loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> users = dataSnapshot.getChildren();

                        for (DataSnapshot user:users){

                            User loginUser = user.getValue(User.class);
                            Log.i(TAG, loginUser.email);

                            if(email.equals(loginUser.email) && password.equals(loginUser.password)){
                                System.out.println("Login Success");
                                loginFound = true;
                                return;
                            }
                        }
                        if(!loginFound){
                            System.out.println("Login Failed!  User doesn't exists");
                            loginEmail.setText("");
                            loginEmail.setHint("Login Failed, please try again!");
                            loginEmail.setHintTextColor(getResources().getColor(R.color.error));
                            loginPW.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    public void map (View view){

        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }
}
