package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        LinearLayout layoutItems = findViewById(R.id.layoutItems);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("budget");
        Query queryAll = databaseReference.orderByKey();
        queryAll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child: snapshot.getChildren()) {
                    String uid = SharedPrefs.getCurrentUserId(Budget.this);
                    if(snapshot.exists()) {
                        if(child.child("userID").getValue().toString().equals(uid)) {
                            String itemID = child.child("itemID").getValue().toString();
                            TextView textView = new TextView(Budget.this);
                            textView.setText(itemID);
                            textView.setBackgroundColor(Color.parseColor("#FF0000"));
                            textView.setTextColor(Color.parseColor("#00FF00"));

                            layoutItems.addView(textView);
                            Toast.makeText(getApplicationContext(), itemID, Toast.LENGTH_SHORT).show();
                        }
                        else  {
                            break;
                        }
                    }
                    else {
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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