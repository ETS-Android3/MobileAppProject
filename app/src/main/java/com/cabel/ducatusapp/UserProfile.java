package com.cabel.ducatusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserProfile extends AppCompatActivity {
    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = new SharedPrefs(this);
        if(sharedPrefs.loadDarkModeTheme()) {
            setTheme(R.style.AppThemeDark);
        }
        else {
            setTheme(R.style.SettingsLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        EditText etvUsernameProfile = (EditText) findViewById(R.id.etvUsernameProfile);
        EditText etvEmailProfile = (EditText) findViewById(R.id.etvEmailProfile);

        //back button intent of User Profile -> home screen
        Button btnBackUserProfile = (Button) findViewById(R.id.btnBackUserProfile);
        btnBackUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, Settings.class);
                startActivity(intent);
            }
        });
    }
}