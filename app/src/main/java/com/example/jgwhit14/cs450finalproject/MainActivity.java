package com.example.jgwhit14.cs450finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button signupBtn, loginBtn;
    private String loginEmail, loginPW, signUpEmail, signUpName, signUpPW1, signUpPW2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signupBtn = findViewById(R.id.signUpBtn);
        loginBtn = findViewById(R.id.loginBtn);

    }
}
