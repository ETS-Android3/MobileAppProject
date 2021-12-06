package com.cabel.ducatusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ExpensesItem extends AppCompatActivity {
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
        setContentView(R.layout.activity_expenses_item);

        if(SharedPrefs.getCurrentUserId(ExpensesItem.this) == null) {
            startActivity(new Intent(ExpensesItem.this, LogIn.class));
        }

        EditText etvCategory = findViewById(R.id.etvCategory);
        EditText etvAmount = findViewById(R.id.etvAmount);
        EditText etvDate = findViewById(R.id.etvDate);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = etvCategory.getText().toString();
                String amount = etvAmount.getText().toString();
                String date = etvDate.getText().toString();

                
            }
        });

        //back button intent
        Button btnBackExpenses = findViewById(R.id.btnBackExpenses);
        btnBackExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpensesItem.this, Expenses.class);
                startActivity(intent);
            }
        });
    }
}