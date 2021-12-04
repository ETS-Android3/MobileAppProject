package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.Change;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ChangePassword extends AppCompatActivity {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    private SharedPrefs sharedPrefs;
    private Boolean key = false;
    private Boolean passVisibility1 = false;
    private Boolean passVisibility2 = false;
    private Boolean passVisibility3 = false;
    private String cipherPass = "";
    private String cipherText = "";

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

        if(SharedPrefs.getCurrentUserId(ChangePassword.this) == null) {
            startActivity(new Intent(ChangePassword.this, LogIn.class));
        }

        EditText etvCurrentPass = findViewById(R.id.etvCurrentPass);
        EditText etvNewPass = findViewById(R.id.etvNewPass);
        EditText etvConfNewPass = findViewById(R.id.etvConfNewPass);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnCancel = findViewById(R.id.btnCancel);
        ImageButton showPass1 = findViewById(R.id.imgPassVisibility1);
        ImageButton showPass2 = findViewById(R.id.imgPassVisibility2);
        ImageButton showPass3 = findViewById(R.id.imgPassVisibility3);

        showPass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passVisibility1) {
                    passVisibility1 = true;
                    etvCurrentPass.setTransformationMethod(new PasswordTransformationMethod());
                    showPass1.setImageResource(R.drawable.ic_visibility_off);
                }
                else {
                    passVisibility1 = false;
                    etvCurrentPass.setTransformationMethod(null);
                    showPass1.setImageResource(R.drawable.ic_visibility);
                }
            }
        });

        showPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passVisibility2) {
                    passVisibility2 = true;
                    etvNewPass.setTransformationMethod(new PasswordTransformationMethod());
                    showPass2.setImageResource(R.drawable.ic_visibility_off);
                }
                else {
                    passVisibility2 = false;
                    etvNewPass.setTransformationMethod(null);
                    showPass2.setImageResource(R.drawable.ic_visibility);
                }
            }
        });
        showPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passVisibility3) {
                    passVisibility3 = true;
                    etvConfNewPass.setTransformationMethod(new PasswordTransformationMethod());
                    showPass3.setImageResource(R.drawable.ic_visibility_off);
                }
                else {
                    passVisibility3 = false;
                    etvConfNewPass.setTransformationMethod(null);
                    showPass3.setImageResource(R.drawable.ic_visibility);
                }
            }
        });

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
                        try {
                            cipherPass = decrypt(snapshot.child("password").getValue(String.class));
                        }
                        catch(Exception e) {
                            System.out.println("Error: " + e);
                        }

                        if(cipherPass.equals(currentPassword)) {
                            key = true; //set key as valid
                            try {
                                cipherText = encrypt(newPassword);
                            }
                            catch(Exception e) {
                                System.out.println("Error: " + e);
                            }

                            Toast.makeText(getApplicationContext(), "Successfully changed password", Toast.LENGTH_SHORT).show();
                            databaseReference.child("/password").setValue(cipherText);
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

    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ChangePassword.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    public static String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ChangePassword.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;

    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(ChangePassword.KEY.getBytes(),ChangePassword.ALGORITHM);
        return key;
    }
}