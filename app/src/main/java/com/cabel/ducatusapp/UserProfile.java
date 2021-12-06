package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.UUID;

public class UserProfile extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private String uid;

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
        setContentView(R.layout.activity_user_profile);

        if(SharedPrefs.getCurrentUserId(UserProfile.this) == null) {
            startActivity(new Intent(UserProfile.this, LogIn.class));
        }

        EditText etvUsernameProfile = findViewById(R.id.etvUsernameProfile);
        EditText etvEmailProfile = findViewById(R.id.etvEmailProfile);

        etvUsernameProfile.setText(SharedPrefs.getCurrentUser(UserProfile.this));
        uid = SharedPrefs.getCurrentUserId(UserProfile.this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users/" + uid);
        Query query = databaseReference.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dbEmail = snapshot.child("email").getValue(String.class);
                etvEmailProfile.setText(dbEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etvEmailProfile.getText().toString();
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                builder.setCancelable(true);
                builder.setTitle("User details");
                builder.setMessage("Save details?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        databaseReference.child("email").setValue(email);
                                        Toast.makeText(getApplicationContext(), "Successfully changed email", Toast.LENGTH_SHORT).show();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(UserProfile.this, UserProfile.class));
                                            }
                                        }, 1000);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println("Failed: " + error.getCode());
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

        //back button intent of User Profile -> home screen
        Button btnBackUserProfile = findViewById(R.id.btnBackUserProfile);
        btnBackUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedPrefs.getUsertype(UserProfile.this).equals("user")) {
                    startActivity(new Intent(UserProfile.this, Settings.class));
                }
                else if(SharedPrefs.getUsertype(UserProfile.this).equals("admin")) {
                    startActivity(new Intent(UserProfile.this, SettingsAdmin.class));
                }
            }
        });
    }

}