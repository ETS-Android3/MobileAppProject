package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BudgetItem extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private int itemID = 0;
    private int userID;
    private float available = 0;

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

        if(SharedPrefs.getCurrentUserId(BudgetItem.this) == null) {
            startActivity(new Intent(BudgetItem.this, LogIn.class));
        }

        TextView tvWarning = findViewById(R.id.tvWarning);
        EditText etvCategory = findViewById(R.id.etvCategory);
        EditText etvDescription = findViewById(R.id.etvDescription);
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
                    if(Float.parseFloat(activity) > Float.parseFloat(budget)) {
                        etvAvailable.setText(null);
                        tvWarning.setVisibility(View.VISIBLE);
                    }
                    else {
                        available = Float.parseFloat(budget) - Float.parseFloat(activity);
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
                float budget = Float.parseFloat(etvBudget.getText().toString());
                float activity = Float.parseFloat(etvActivity.getText().toString());
                String description = etvDescription.getText().toString();

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MM/dd/yyyy");
                String date = month_date.format(cal.getTime());

                available = budget - activity;

                //Check if fields are empty
                if (TextUtils.isEmpty(category)) {
                    Toast.makeText(getApplicationContext(), "Enter category", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(getApplicationContext(), "Enter description", Toast.LENGTH_SHORT).show();
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
                DatabaseReference expensesReference = FirebaseDatabase.getInstance().getReference().child("expenses");
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

                AlertDialog.Builder builder = new AlertDialog.Builder(BudgetItem.this);
                builder.setCancelable(true);
                builder.setTitle("Add Item");
                builder.setMessage("Save details?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Query queryAll = databaseReference.orderByKey();
                                queryAll.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        userID = Integer.parseInt(SharedPrefs.getCurrentUserId(BudgetItem.this));
                                        BudgetItemClass budgetItem = new BudgetItemClass(itemID, category, description, budget, activity, available, date, userID);
                                        databaseReference.child(String.valueOf(itemID)).setValue(budgetItem);

                                        ExpensesClass expensesClass = new ExpensesClass(itemID, category, description, activity, date, userID, itemID);
                                        expensesReference.child(String.valueOf(itemID)).setValue(expensesClass);

                                        startActivity(new Intent(getApplicationContext(), Budget.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        //back button intent
        Button btnBackBudgeted = (Button) findViewById(R.id.btnBackBudgeted);
        btnBackBudgeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BudgetItem.this, Budget.class));
            }
        });

    }
}