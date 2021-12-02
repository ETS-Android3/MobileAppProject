package com.cabel.ducatusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Developers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);

        if(SharedPrefs.getCurrentUserId(Developers.this) == null) {
            startActivity(new Intent(Developers.this, LogIn.class));
        }

        if(!SharedPrefs.getUsertype(Developers.this).equals("admin")) {
            startActivity(new Intent(Developers.this, LogIn.class));
        }

        Button btnBackDevs = (Button) findViewById(R.id.btnBackDevs);
        btnBackDevs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Developers.this, Settings.class);
                startActivity(intent);
            }
        });
    }
}