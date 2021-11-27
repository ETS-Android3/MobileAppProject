package com.cabel.ducatusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Home extends AppCompatActivity {
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
        setContentView(R.layout.activity_home);

        // Menu / Profile Settings (intent)
        Button btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Settings.class));
            }
        });

        TextView tvUser = (TextView) findViewById(R.id.tvUser);
        tvUser.setText(SharedPrefs.getCurrentUser(Home.this));

        //To be Budgeted (intent)
        RelativeLayout RLBudgeted = (RelativeLayout) findViewById(R.id.RLBudgeted);
        RLBudgeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Budget.class));
            }
        });

        //Expenses (intent)
        RelativeLayout RLExpenses = (RelativeLayout) findViewById(R.id.RLExpenses);
        RLExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Expenses.class));
            }
        });

        //Tips (intent)
        RelativeLayout RLTips = (RelativeLayout) findViewById(R.id.RLTips);
        RLTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Tips.class));
            }
        });

    }
}