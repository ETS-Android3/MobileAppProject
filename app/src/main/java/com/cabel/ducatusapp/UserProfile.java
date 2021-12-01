package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {
    private SharedPrefs sharedPrefs;

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
        setContentView(R.layout.activity_user_profile);

        EditText etvUsernameProfile = findViewById(R.id.etvUsernameProfile);
        EditText etvEmailProfile = findViewById(R.id.etvEmailProfile);

        etvUsernameProfile.setText(SharedPrefs.getCurrentUser(UserProfile.this));
        String uid = SharedPrefs.getCurrentUserId(UserProfile.this);
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
                Intent intent = new Intent(UserProfile.this, Settings.class);
                startActivity(intent);
            }
        });
    }
}