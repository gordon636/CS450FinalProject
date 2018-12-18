package com.example.jgwhit14.cs450finalproject;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int style;
    private SeekBar radiusBar;
    private TextView radiusTextView;
    private Switch switchRadius;

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
        int currentRadius = pref.getInt("locationRadius",30);

        radiusBar = findViewById(R.id.seekBarRadius);
        radiusTextView =findViewById(R.id.textViewRadiusValue);

        radiusBar.setProgress(currentRadius);
        radiusTextView.setText(currentRadius + " km");
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                editor.putInt("locationRadius",i).apply();
                radiusTextView.setText(pref.getInt("locationRadius",30) + " km");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radiusTextView.setText(radiusTextView.getText().toString());

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


        switchRadius= findViewById(R.id.switchRadius);
        if (pref.getString("radiusOn", "true").equals("true")){

            switchRadius.setChecked(true);
        }else{
            switchRadius.setChecked(false);
        }

        switchRadius.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    switchRadius.setChecked(true);
                    pref.edit().putString("radiusOn","true").apply();
                }else{
                    switchRadius.setChecked(false);
                    pref.edit().putString("radiusOn","false").apply();
                }
            }
        });

    }
}
