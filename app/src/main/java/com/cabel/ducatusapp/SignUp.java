package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class SignUp extends AppCompatActivity {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    private SharedPrefs sharedPrefs;
    private Integer userID = 0;
    private Boolean validKey = false;
    private Boolean passVisibility = false;

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
        setContentView(R.layout.activity_signup);

        TextView tvWarning = findViewById(R.id.tvWarning);
        EditText etvUsername = findViewById(R.id.etvUserReg);
        EditText etvEmail = findViewById(R.id.etvEmailReg);
        EditText etvPassword = findViewById(R.id.etvPassReg);
        ImageButton showPass = findViewById(R.id.imgPassVisibility);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passVisibility) {
                    passVisibility = true;
                    etvPassword.setTransformationMethod(new PasswordTransformationMethod());
                    showPass.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                }
                else {
                    passVisibility = false;
                    etvPassword.setTransformationMethod(null);
                    showPass.setImageResource(R.drawable.ic_baseline_visibility_24);
                }
            }
        });

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

                //username and password must not be same
                if(username.equals(password)) {
                    Toast.makeText(getApplicationContext(), "Username and password must not be the same", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check email validity
                if(!email.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Email is invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                //email and password must not be same
                if(email.equals(password)) {
                    Toast.makeText(getApplicationContext(), "Email and password must not be the same", Toast.LENGTH_SHORT).show();
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
                        for (DataSnapshot child : snapshot.getChildren()) {
                            if(snapshot.exists()) {
                                if (child.child("username").getValue(String.class).equals(username)) { //check if username already exists
                                    if (validKey.equals(false)) { //check validity of key to avoid late data retrieval
                                        tvWarning.setText("Username is already in use");
                                        tvWarning.setVisibility(View.VISIBLE);
                                        //Toast.makeText(getApplicationContext(), "Username is already in use", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    if (child.child("email").getValue(String.class).equals(email)) { //check if email already exists
                                        if (validKey.equals(false)) { //check validity of key to avoid late data retrieval
                                            tvWarning.setText("Email is already in use");
                                            tvWarning.setVisibility(View.VISIBLE);
                                            //Toast.makeText(getApplicationContext(), "Email is already in use", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else { //valid credentials -> insert to database
                                        validKey = true; //set credentials as valid
                                        try {
                                            String cipherText = encrypt(password);
                                            User user = new User(userID, username, email, cipherText, "user");
                                            databaseReference.child(userID.toString()).setValue(user);
                                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                                            SharedPrefs.setLoginStatus(SignUp.this, true);
                                            SharedPrefs.setUsertype(SignUp.this, "user");
                                            SharedPrefs.setCurrentUser(SignUp.this, username);
                                            SharedPrefs.setCurrentUserId(SignUp.this, userID.toString());
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                                    finish();
                                                }
                                            }, 2000);

                                        }
                                        catch(Exception e) {
                                            System.out.println("Error: " + e);
                                        }
                                    }
                                }
                            }
                            else {
                                break;
                            }
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

    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(SignUp.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    public static String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(SignUp.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;

    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(SignUp.KEY.getBytes(),SignUp.ALGORITHM);
        return key;
    }
}