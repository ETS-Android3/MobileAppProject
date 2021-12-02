package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

                            CardView cardView = new CardView(Budget.this);
                            CardView.LayoutParams cardParam = new CardView.LayoutParams(
                                    300,
                                    64
                            );
                            cardParam.setMargins(30, 10, 30, 0);
                            cardView.setLayoutParams(cardParam);
                            cardView.setId(R.id.cardview);
                            cardView.setRadius(13.125f);
                            cardView.setCardElevation(15.75f);

                            RelativeLayout rl = new RelativeLayout(Budget.this);
                            RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT
                            );
                            rl.setLayoutParams(rlParam);
                            rl.setId(R.id.rl);
                            cardView.addView(rl);
                            /*LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            ViewGroup viewGroup = findViewById(R.id.cardview);
                            inflater.inflate(R.id.rl, viewGroup);*/

                            TextView category = new TextView(Budget.this);
                            RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(60, 18);
                            textParam.setMargins(133, 11, 158, 29);
                            category.setLayoutParams(textParam);
                            category.setText("Yes");

                            category.setTextColor(Color.parseColor("#5E5B5B"));
                            rl.addView(category);

                            layoutItems.addView(cardView);
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