package com.cabel.ducatusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TipsItem extends AppCompatActivity {
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
        setContentView(R.layout.activity_tips_item);

        TextView tipsTitle = findViewById(R.id.tipsTitle);
        TextView tipsContent = findViewById(R.id.tipsContent);
        ImageView imgTips = findViewById(R.id.imgTips);

        Intent i = getIntent();

        tipsTitle.setText(i.getStringExtra("title"));
        tipsContent.setText("     " + i.getStringExtra("message"));
        int image_link = i.getIntExtra("image_url",0);
        imgTips.setImageResource(image_link);


        //Back button intent
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TipsItem.this, Tips.class));
            }
        });

        //Home icon intent
        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TipsItem.this, Home.class));
            }
        });

        //Budgeted icon intent
        Button btnBudgeted = findViewById(R.id.btnBudgeted);
        btnBudgeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TipsItem.this, Budget.class));
            }
        });

        //Expenses icon intent
        Button btnExpenses = findViewById(R.id.btnExpenses);
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TipsItem.this, Expenses.class));
            }
        });

        //Tips icon intent
        Button btnTips = findViewById(R.id.btnTips);
        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TipsItem.this, Tips.class));
            }
        });
    }
}