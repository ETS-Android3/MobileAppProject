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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassword extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private User user;
    private Boolean emailKey = false;

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
        setContentView(R.layout.activity_forgot_password);

        EditText etvEmail = (EditText) findViewById(R.id.etvEmail);
        EditText etvNewPassword = (EditText) findViewById(R.id.etvNewPassReset);
        EditText etvConfirmPassword = (EditText) findViewById(R.id.etvConfirmNewPassReset);
        Button btnReset = (Button) findViewById(R.id.btnResetPass);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etvEmail.getText().toString();
                String newPassword = etvNewPassword.getText().toString();
                String confirmPassword = etvConfirmPassword.getText().toString();

                //check if fields are empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
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

                //check email validity
                if(!email.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Email is invalid", Toast.LENGTH_SHORT).show();
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

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                Query queryAll = databaseReference.orderByKey();
                queryAll.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Thread.sleep(0);
                            for(DataSnapshot uniqueKey: snapshot.getChildren()) {
                                if(uniqueKey.child("email").getValue(String.class).equals(email)) { //retrieve inputted email
                                    String userKey = uniqueKey.getKey(); //retrieve the key that contains the email
                                    emailKey = true; //set email as valid
                                    for(DataSnapshot child: uniqueKey.getChildren()) {
                                        databaseReference.child(userKey + "/password").setValue(newPassword);
                                        Toast.makeText(getApplicationContext(), "Successfully changed password", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPassword.this, LogIn.class));
                                    }
                                }
                                else {
                                    if(emailKey.equals(false)) { //check validity of email to avoid late data retrieval
                                        Toast.makeText(getApplicationContext(), "Email does not exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Failed: " + error.getCode());
                    }
                });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword.this, LogIn.class);
                startActivity(intent);
            }
        });
    }
}