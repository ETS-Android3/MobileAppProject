package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    private ImageView imgProfilePic;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference storageURLRef;

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

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imgProfilePic = findViewById(R.id.imgProfilePic);
        EditText etvUsernameProfile = findViewById(R.id.etvUsernameProfile);
        EditText etvEmailProfile = findViewById(R.id.etvEmailProfile);

        etvUsernameProfile.setText(SharedPrefs.getCurrentUser(UserProfile.this));
        uid = SharedPrefs.getCurrentUserId(UserProfile.this);

        DatabaseReference imageReference = FirebaseDatabase.getInstance().getReference().child("images/" + uid);
        imageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String urlReference = snapshot.child("imageurl").getValue(String.class);
                String url = "gs://ducatusfirebase.appspot.com/images/1c173ed3-7aa0-467f-8e24-90c0f0031db1";
                //storageURLRef = storageURLRef.child("images/" + urlReference);

                Glide.with(UserProfile.this).load(url).into(imgProfilePic);
                //Toast.makeText(getApplicationContext(), storageURLRef, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

        //back button intent of User Profile -> home screen
        Button btnBackUserProfile = findViewById(R.id.btnBackUserProfile);
        btnBackUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfile.this, Settings.class));
            }
        });
    }

}