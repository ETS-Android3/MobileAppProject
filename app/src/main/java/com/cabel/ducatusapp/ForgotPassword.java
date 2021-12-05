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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ForgotPassword extends AppCompatActivity {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    private SharedPrefs sharedPrefs;
    private Boolean emailKey = false;
    private Boolean passVisibility1 = false;
    private Boolean passVisibility2 = false;
    private String cipherPass = "";

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
        setContentView(R.layout.activity_forgot_password);

        EditText etvEmail = findViewById(R.id.etvEmail);
        EditText etvNewPassword = findViewById(R.id.etvNewPassReset);
        EditText etvConfirmPassword = findViewById(R.id.etvConfirmNewPassReset);
        Button btnReset = findViewById(R.id.btnResetPass);
        Button btnCancel = findViewById(R.id.btnCancel);
        ImageButton showPass1 = findViewById(R.id.imgPassVisibility1);
        ImageButton showPass2 = findViewById(R.id.imgPassVisibility2);

        showPass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passVisibility1) {
                    passVisibility1 = true;
                    etvNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                    showPass1.setImageResource(R.drawable.ic_visibility_off);
                }
                else {
                    passVisibility1 = false;
                    etvNewPassword.setTransformationMethod(null);
                    showPass1.setImageResource(R.drawable.ic_visibility);
                }
            }
        });

        showPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passVisibility2) {
                    passVisibility2 = true;
                    etvConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    showPass2.setImageResource(R.drawable.ic_visibility_off);
                }
                else {
                    passVisibility2 = false;
                    etvConfirmPassword.setTransformationMethod(null);
                    showPass2.setImageResource(R.drawable.ic_visibility);
                }
            }
        });

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

                //email and password must not be same
                if(email.equals(newPassword)) {
                    Toast.makeText(getApplicationContext(), "Email and password must not be the same", Toast.LENGTH_SHORT).show();
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
                                    try {
                                        cipherPass = encrypt(newPassword);
                                    }
                                    catch(Exception e) {
                                        System.out.println("Error: " + e);
                                    }
                                    databaseReference.child(userKey + "/password").setValue(cipherPass);
                                    Toast.makeText(getApplicationContext(), "Successfully changed password", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ForgotPassword.this, LogIn.class));
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

    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ForgotPassword.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    public static String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ForgotPassword.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;

    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(ForgotPassword.KEY.getBytes(),ForgotPassword.ALGORITHM);
        return key;
    }

}