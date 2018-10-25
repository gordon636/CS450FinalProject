package com.example.jgwhit14.cs450finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button signupBtn, loginBtn;
    private EditText loginEmail, loginPW, signUpEmail, signUpName, signUpPW1, signUpPW2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                String password = signUpEmail.getText().toString();


            }
        });

    }
}
