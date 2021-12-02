package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        if(SharedPrefs.getCurrentUserId(Home.this) == null) {
            startActivity(new Intent(Home.this, LogIn.class));
        }

        TextView tvBudgetItems = findViewById(R.id.tvBudgetItems);
        TextView tvExpenseItems = findViewById(R.id.tvExpenseItems);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("budget");
        Query queryBudget = databaseReference.orderByKey();
        queryBudget.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = SharedPrefs.getCurrentUserId(Home.this);
                int items = 0;
                for(DataSnapshot child: snapshot.getChildren()) {
                    if(snapshot.exists()) {
                        if(child.child("userID").getValue().toString().equals(uid)) { // get items created by current user and count
                            items++;
                        }
                        else {
                            break;
                        }
                    }
                    else {
                        break;
                    }
                }
                if(items == 1) {
                    tvBudgetItems.setText(items + " item");
                }
                else {
                    tvBudgetItems.setText(items + " items");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("expenses");
        Query queryExpense = databaseReference.orderByKey();
        queryExpense.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = SharedPrefs.getCurrentUserId(Home.this);
                int items = 0;
                for(DataSnapshot child: snapshot.getChildren()) {
                    if(snapshot.exists()) {
                        if(child.child("userID").getValue().toString().equals(uid)) { // get items created by current user and count
                            items++;
                        }
                        else {
                            break;
                        }
                    }
                    else {
                        break;
                    }
                }
                if(items == 1) {
                    tvExpenseItems.setText(items + " item");
                }
                else {
                    tvExpenseItems.setText(items + " items");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Menu / Profile Settings (intent)
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedPrefs.getUsertype(Home.this).equals("user")) {
                    startActivity(new Intent(Home.this, Settings.class));
                }
                else if(SharedPrefs.getUsertype(Home.this).equals("admin")) {
                    startActivity(new Intent(Home.this, SettingsAdmin.class));
                }

            }
        });

        TextView tvUser = findViewById(R.id.tvUser);
        tvUser.setText(SharedPrefs.getCurrentUser(Home.this));

        //To be Budgeted (intent)
        RelativeLayout RLBudgeted = findViewById(R.id.RLBudgeted);
        RLBudgeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Budget.class));
            }
        });

        //Expenses (intent)
        RelativeLayout RLExpenses = findViewById(R.id.RLExpenses);
        RLExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Expenses.class));
            }
        });

        //Tips (intent)
        RelativeLayout RLTips = findViewById(R.id.RLTips);
        RLTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Tips.class));
            }
        });

    }
}