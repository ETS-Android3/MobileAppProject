package com.cabel.ducatusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Budget extends AppCompatActivity {
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
        setContentView(R.layout.activity_budget);

        //Home icon intent
        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, Home.class);
                startActivity(intent);
            }
        });

        //Expenses icon intent
        Button btnExpenses = findViewById(R.id.btnExpenses);
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, Expenses.class);
                startActivity(intent);
            }
        });

        //Tips icon intent
        Button btnTips = findViewById(R.id.btnTips);
        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, Tips.class);
                startActivity(intent);
            }
        });

        //Add icon intent
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, BudgetItem.class);
                startActivity(intent);
            }
        });
    }
}