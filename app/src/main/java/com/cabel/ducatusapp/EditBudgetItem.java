package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
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

import java.util.regex.Pattern;

public class EditBudgetItem extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private String itemID = "";
    private String category = "";
    private String description = "";
    private float budget = 0;
    private float activity = 0;
    private float available = 0;
    private Boolean activityKey = false;

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
        setContentView(R.layout.activity_edit_budget_item);

        if(SharedPrefs.getCurrentUserId(EditBudgetItem.this) == null) {
            startActivity(new Intent(EditBudgetItem.this, LogIn.class));
        }

        TextView tvWarning = findViewById(R.id.tvWarning);
        EditText etvCategory = findViewById(R.id.etvCategory);
        EditText etvDescription = findViewById(R.id.etvDescription);
        EditText etvBudget = findViewById(R.id.etvBudget);
        EditText etvActivity = findViewById(R.id.etvActivity);
        EditText etvAvailable = findViewById(R.id.etvAvailable);

        Intent i = getIntent();
        itemID = i.getStringExtra("itemID");
        category = i.getStringExtra("category");
        description = i.getStringExtra("description");
        budget = i.getFloatExtra("budget", 0);
        activity = i.getFloatExtra("activity", 0);
        available = i.getFloatExtra("available", 0);

        etvCategory.setText(category);
        etvDescription.setText(description);
        etvBudget.setText(String.format("%.2f", available));

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

                if(TextUtils.isEmpty(activity)) {
                    activityKey = false;
                    etvAvailable.setText(null);
                    tvWarning.setText("");
                    tvWarning.setVisibility(View.INVISIBLE);
                }
                else if (!Pattern.matches("^[0-9].*", activity)) {
                    activityKey = false;
                    tvWarning.setText("Input must be a number");
                    tvWarning.setVisibility(View.VISIBLE);
                }
                else {
                    if (!TextUtils.isEmpty(activity)) {
                        if (Float.parseFloat(activity) > Float.parseFloat(budget)) {
                            activityKey = false;
                            etvAvailable.setText(null);
                            tvWarning.setText("Budget must be higher than To Spend amount");
                            tvWarning.setVisibility(View.VISIBLE);
                        }
                        else {
                            activityKey = true;
                            available = Float.parseFloat(budget) - Float.parseFloat(activity);
                            etvAvailable.setText(String.valueOf(available));
                            tvWarning.setText("");
                            tvWarning.setVisibility(View.INVISIBLE);
                        }
                    }

                }
            }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strActivity = etvActivity.getText().toString();
                category = etvCategory.getText().toString();
                description = etvDescription.getText().toString();

                //Check if fields are empty
                if (TextUtils.isEmpty(category)) {
                    Toast.makeText(getApplicationContext(), "Enter category", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(getApplicationContext(), "Enter description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(strActivity)) {
                    Toast.makeText(getApplicationContext(), "Enter activity", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(activityKey.equals(true)) {
                    activity = Float.parseFloat(etvActivity.getText().toString());

                    //check email validity
                    if(activity > budget) {
                        Toast.makeText(getApplicationContext(), "Activity must be lower than the allocated budget", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    available = budget - activity;

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditBudgetItem.this);
                    builder.setCancelable(true);
                    builder.setTitle("Edit Item");
                    builder.setMessage("Save details?");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("budget/" + itemID);
                                    Query queryItem = databaseReference.orderByKey();
                                    queryItem.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            float dbActivity = Float.parseFloat(snapshot.child("activity").getValue().toString());
                                            float dbAvailable = Float.parseFloat(snapshot.child("available").getValue().toString());

                                            activity += dbActivity;
                                            available -= dbAvailable;

                                            databaseReference.child("/category").setValue(category);
                                            databaseReference.child("/description").setValue(description);
                                            databaseReference.child("/activity").setValue(activity);
                                            databaseReference.child("/available").setValue(available);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            System.out.println("Error: " + error);
                                        }

                                    });
                                    DatabaseReference expensesReference = FirebaseDatabase.getInstance().getReference().child("expenses");
                                    Query expensesItem = expensesReference.orderByKey();
                                    expensesItem.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot child: snapshot.getChildren()) {
                                                if(child.child("itemID").getValue().toString().equals(itemID)) {
                                                    String expenseID = child.child("expenseID").getValue().toString();
                                                    expensesReference.child(expenseID + "/category").setValue(category);
                                                    expensesReference.child(expenseID + "/description").setValue(description);
                                                    expensesReference.child(expenseID + "/amount").setValue(activity);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            System.out.println("Error: " + error);
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(), "Item details saved", Toast.LENGTH_SHORT).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(EditBudgetItem.this, Budget.class));
                                        }
                                    }, 1000);
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
            }
        });

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditBudgetItem.this);
                builder.setCancelable(true);
                builder.setTitle("Delete Item");
                builder.setMessage("Delete this item?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("budget/" + itemID);
                                Query queryItem = databaseReference.orderByKey();
                                queryItem.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println("Error: " + error);
                                    }
                                });

                                DatabaseReference expensesReference = FirebaseDatabase.getInstance().getReference().child("expenses");
                                Query expensesItem = expensesReference.orderByKey();
                                expensesItem.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot child: snapshot.getChildren()) {
                                            if(child.child("itemID").getValue().toString().equals(itemID)) {
                                                snapshot.getRef().removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println("Error: " + error);
                                    }
                                });

                                Toast.makeText(getApplicationContext(), "Deleted item", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(EditBudgetItem.this, Budget.class));
                                    }
                                }, 1000);
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

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Budget.class));
            }
        });
    }
}