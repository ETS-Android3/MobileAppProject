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

public class EditBudgetItem extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private int itemID = 0;
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("budget");
                Query queryAll = databaseReference.orderByKey();
                queryAll.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child: snapshot.getChildren()) {
                            if(child.child("itemID").getValue().toString().equals(itemID)) {
                                category = child.child("category").getValue().toString();
                                description = child.child("description").getValue().toString();
                                budget = Float.parseFloat(child.child("budget").getValue().toString());
                                activity = Float.parseFloat(child.child("activity").getValue().toString());
                                available = Float.parseFloat(child.child("available").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                etvCategory.setText(category);
                etvDescription.setText(description);
                etvBudget.setText(String.format("%.2f", budget));
                etvActivity.setText(String.format("%.2f", activity));
                etvAvailable.setText(String.format("%.2f", available));

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


            }
        });
    }
}