package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MyLocations extends AppCompatActivity {

    private RecyclerView recyclerView;

  //  private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locations);

/*
        this.recyclerView = findViewById(R.id.rv_locations);

        LinearLayoutManager layoutManager =

                new LinearLayoutManager(this,

                        LinearLayoutManager.VERTICAL,

                        false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

     //   this.imageAdapter =

       //         new ImageAdapter(ImageURLInterface

         //               .create(ImageURLInterface.GOOGLE),this);



//        recyclerView.setAdapter(this.imageAdapter);

        recyclerView.addItemDecoration(

                new DividerItemDecoration(this,

                        DividerItemDecoration.VERTICAL));

                        */
    }

    public void share (View view){

        //share my location on other social media platforms
    }

    public void friends (View view){

        Intent intent = new Intent(this, Friends.class);
        startActivity(intent);
    }

    public void clear (View view){

      //clear my saved locations

    }


}
