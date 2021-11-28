package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private Boolean key = false;
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
        setContentView(R.layout.activity_change_password);

        EditText etvCurrentPass = findViewById(R.id.etvCurrentPass);
        EditText etvNewPass = findViewById(R.id.etvNewPass);
        EditText etvConfNewPass = findViewById(R.id.etvConfNewPass);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = etvCurrentPass.getText().toString();
                String newPassword = etvNewPass.getText().toString();
                String confirmPassword = etvConfNewPass.getText().toString();

                if (TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter current password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Confirm your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if new password is the same as current password
                if(currentPassword.equals(newPassword)) {
                    Toast.makeText(getApplicationContext(), "New password must not be the same as current password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if password fields match
                if(!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check password validity
                if(newPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = SharedPrefs.getCurrentUserId(ChangePassword.this); //retrieve current user's userID


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users/" + uid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("password").getValue(String.class).equals(currentPassword)) {
                            key = true; //set key as valid
                            Toast.makeText(getApplicationContext(), "Successfully changed password", Toast.LENGTH_SHORT).show();
                            databaseReference.child("/password").setValue(newPassword);
                            startActivity(new Intent(ChangePassword.this, Settings.class));
                        }
                        else {
                            if(key.equals(false)) { //check if key is valid to avoid late data retrieval
                                Toast.makeText(getApplicationContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Failed: " + error.getCode());
                    }
                });

                //startActivity(new Intent(ChangePassword.this, HomeScreen.class));
            }
        });

        //back button intent of User Profile -> home screen
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePassword.this, Settings.class));
            }
        });

    }
}