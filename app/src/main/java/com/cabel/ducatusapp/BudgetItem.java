package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BudgetItem extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private int itemID = 0;
    private int userID;
    private int available = 0;

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
        setContentView(R.layout.activity_budget_item);

        TextView tvWarning = findViewById(R.id.tvWarning);
        EditText etvCategory = findViewById(R.id.etvCategory);
        EditText etvBudget = findViewById(R.id.etvBudget);
        EditText etvActivity = findViewById(R.id.etvActivity);
        EditText etvAvailable = findViewById(R.id.etvAvailable);

        etvActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String budget = etvBudget.getText().toString();
                String activity = etvActivity.getText().toString();

                if(!TextUtils.isEmpty(activity)) {
                    if(Integer.parseInt(activity) > Integer.parseInt(budget)) {
                        etvAvailable.setText(null);
                        tvWarning.setVisibility(View.VISIBLE);
                    }
                    else {
                        available = Integer.parseInt(budget) - Integer.parseInt(activity);
                        etvAvailable.setText(String.valueOf(available));
                        tvWarning.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    etvAvailable.setText(null);
                }
            }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = etvCategory.getText().toString();
                int budget = Integer.parseInt(etvBudget.getText().toString());
                int activity = Integer.parseInt(etvActivity.getText().toString());
                String description = "";

                available = budget - activity;

                //Check if fields are empty
                if (TextUtils.isEmpty(category)) {
                    Toast.makeText(getApplicationContext(), "Enter category", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(String.valueOf(budget))) {
                    Toast.makeText(getApplicationContext(), "Enter budget", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(String.valueOf(activity))) {
                    Toast.makeText(getApplicationContext(), "Enter activity", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check email validity
                if(activity > budget) {
                    Toast.makeText(getApplicationContext(), "Activity must be lower than the allocated budget", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("budget");
                //check last itemID
                Query queryLast = databaseReference.orderByKey().limitToLast(1);
                queryLast.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            if(child.exists()) { //check if firebase is empty
                                try {
                                    //retrieving last userID
                                    String pid = child.child("itemID").getValue().toString();
                                    itemID = Integer.parseInt(pid);
                                    itemID += 1;

                                }
                                catch(NumberFormatException nfe) {
                                    System.out.println("Could not parse " + nfe);
                                }
                            }
                            else {
                                itemID = 0;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Failed: " + error.getCode());
                    }
                });

                Query queryAll = databaseReference.orderByKey();
                queryAll.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userID = Integer.parseInt(SharedPrefs.getCurrentUserId(BudgetItem.this));
                        BudgetItemClass budgetItem = new BudgetItemClass(itemID, category, budget, activity, available, description, userID);
                        databaseReference.child(String.valueOf(itemID)).setValue(budgetItem);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        //back button intent
        Button btnBackBudgeted = (Button) findViewById(R.id.btnBackBudgeted);
        btnBackBudgeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetItem.this, Budget.class);
                startActivity(intent);
            }
        });

    }
}