package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class SignUp extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private Integer userID = 0;
    private Boolean validKey = false;

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
        setContentView(R.layout.activity_signup);

        EditText etvUsername = findViewById(R.id.etvUserReg);
        EditText etvEmail = findViewById(R.id.etvEmailReg);
        EditText etvPassword = findViewById(R.id.etvPassReg);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = etvUsername.getText().toString();
                String email = etvEmail.getText().toString();
                String password = etvPassword.getText().toString();

                //Check if fields are empty
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check email validity
                if(!email.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Email is invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check password validity
                if(password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                //check last userID
                Query queryLast = databaseReference.orderByKey().limitToLast(1);
                queryLast.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            if(child.exists()) { //check if firebase is empty
                                try {
                                    //retrieving last userID
                                    String uid = child.child("userID").getValue().toString();
                                    userID = Integer.parseInt(uid);
                                    userID += 1;

                                }
                                catch(NumberFormatException nfe) {
                                    System.out.println("Could not parse " + nfe);
                                }
                            }
                            else {
                                userID = 0;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Failed: " + error.getCode());
                    }
                });

                //check all data in the database
                Query queryAll = databaseReference.orderByKey();
                queryAll.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Thread.sleep(0);
                            for(DataSnapshot child: snapshot.getChildren()) {
                                if(child.child("username").getValue(String.class).equals(username)) { //check if username already exists
                                    if(validKey.equals(false)) { //check validity of key to avoid late data retrieval
                                        Toast.makeText(getApplicationContext(), "Username is already in use", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    if(child.child("email").getValue(String.class).equals(email)) { //check if email already exists
                                        if(validKey.equals(false)) { //check validity of key to avoid late data retrieval
                                            Toast.makeText(getApplicationContext(), "Email is already in use", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else { //valid credentials -> insert to database
                                        validKey = true; //set credentials as valid
                                        User user = new User(userID, username, email, password, "user");
                                        databaseReference.child(userID.toString()).setValue(user);
                                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                                        SharedPrefs.setLoginStatus(SignUp.this, true);
                                        SharedPrefs.setUsertype(SignUp.this, "user");
                                        SharedPrefs.setCurrentUser(SignUp.this, username);
                                        SharedPrefs.setCurrentUserId(SignUp.this, userID.toString());
                                        startActivity(new Intent(getApplicationContext(), Home.class));
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

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
            }
        });
    }
}