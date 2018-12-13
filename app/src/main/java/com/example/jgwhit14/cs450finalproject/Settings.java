package com.example.jgwhit14.cs450finalproject;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pref = getApplicationContext().getSharedPreferences("Profile",0);
        editor = pref.edit();
        style = pref.getInt("MAP_STYLE", 1);

        Button bt1 = findViewById(R.id.style1);
        Button bt2 = findViewById(R.id.style2);
        Button bt3 = findViewById(R.id.style3);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(style != 1){
                    editor.putInt("MAP_STYLE", 1).apply();
                }
                finish();
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(style != 2){
                    editor.putInt("MAP_STYLE", 2).apply();
                }
                finish();
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(style != 3){
                    editor.putInt("MAP_STYLE", 3).apply();
                }
                finish();
            }
        });
    }
}
