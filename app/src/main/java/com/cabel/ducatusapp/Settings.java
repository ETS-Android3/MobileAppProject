package com.cabel.ducatusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    private SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = new SharedPrefs(this);
        if(sharedPrefs.loadDarkModeTheme()) {
            setTheme(R.style.SettingsDark);
        }
        else {
            setTheme(R.style.SettingsLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvUser = findViewById(R.id.tvUser);
        TextView editPersonalDetails = findViewById(R.id.editPersonalDetails);
        Switch switcher = findViewById(R.id.switcher);
        RelativeLayout RLSwitch = findViewById(R.id.RLSwitch);
        TextView tvChangePassword = findViewById(R.id.tvChangePassword);
        TextView tvLogout = findViewById(R.id.tvLogout);
        TextView tvDevelopers = findViewById(R.id.tvDevelopers);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, Home.class);
                startActivity(intent);
            }
        });

        tvUser.setText(SharedPrefs.getCurrentUser(Settings.this));

        editPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, UserProfile.class));
            }
        });

        if(sharedPrefs.loadDarkModeTheme()) {
            switcher.setChecked(true);
        }
        switcher.setClickable(false);

        RLSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!switcher.isChecked()) {
                    switcher.setChecked(true);
                    sharedPrefs.setDarkModeTheme(true);
                    restartApp();
                }
                else {
                    switcher.setChecked(false);
                    sharedPrefs.setDarkModeTheme(false);
                    restartApp();
                }
            }
        });

        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, ChangePassword.class));
            }
        });

        tvDevelopers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Developers.class));
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.clearData(Settings.this);
                startActivity(new Intent(Settings.this, LogIn.class));
                finish();
            }
        });
    }
    private void restartApp() {
        startActivity(new Intent(getApplicationContext(), Settings.class));
        finish();
    }
}