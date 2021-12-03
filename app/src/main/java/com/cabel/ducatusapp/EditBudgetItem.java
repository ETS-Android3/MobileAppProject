package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class EditBudgetItem extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private String itemID = "";
    private int userID;
    private String category = "";
    private String description = "";
    private float budget = 0;
    private float activity = 0;
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

                                        startActivity(new Intent(EditBudgetItem.this, Budget.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println("Error: " + error);
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
                                        startActivity(new Intent(EditBudgetItem.this, Budget.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println("Error: " + error);
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
    }
}