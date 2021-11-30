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

public class LogIn extends AppCompatActivity {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    private SharedPrefs sharedPrefs;
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private Boolean userKey = false;
    private Boolean passwordKey = false;
    private Boolean passVisibility = false;
    private String firebasePass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = new SharedPrefs(this);
        if(sharedPrefs.loadDarkModeTheme()) {
            setTheme(R.style.AppThemeDark);

        }
        else {
            setTheme(R.style.SettingsLight);

        }

        if(!SharedPrefs.getCurrentUser(LogIn.this).equals("")) {
            startActivity(new Intent(LogIn.this, Home.class));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etvUsername = findViewById(R.id.etvUserReg);
        EditText etvPassword = findViewById(R.id.etvPassReg);
        ImageButton showPass = findViewById(R.id.imgPassVisibility);
        Button btnLogin = findViewById(R.id.btnRegister);
        TextView tvForgotPass = findViewById(R.id.tvForgotPass);
        TextView tvCreateAccount = findViewById(R.id.tvLogin);

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etvUsername.getText().toString();
                String password = etvPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "Loading data...", Toast.LENGTH_LONG).show();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                Query query = databaseReference.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child: snapshot.getChildren()) {
                            try {
                                Thread.sleep(3000);
                                if (child.child("username").getValue(String.class).equals(username)) {
                                    userKey = true; //set user as valid
                                    SharedPrefs.setCurrentUserId(LogIn.this, child.getKey());
                                    try {
                                        firebasePass = decrypt(child.child("password").getValue(String.class));
                                    }
                                    catch(Exception e) {
                                        System.out.println("Error: " + e);
                                    }

                                    if (firebasePass.equals(password)) {
                                        passwordKey = true; //set password as valid
                                        if (child.child("usertype").getValue(String.class).equals("admin")) {
                                            SharedPrefs.setLoginStatus(LogIn.this, true);
                                            SharedPrefs.setUsertype(LogIn.this, "admin");
                                            SharedPrefs.setCurrentUser(LogIn.this, username);
                                            startActivity(new Intent(getApplicationContext(), Home.class));
                                        }
                                        else {
                                            SharedPrefs.setLoginStatus(LogIn.this, true);
                                            SharedPrefs.setUsertype(LogIn.this, "user");
                                            SharedPrefs.setCurrentUser(LogIn.this, username);
                                            startActivity(new Intent(getApplicationContext(), Home.class));
                                        }
                                    }
                                    else {
                                        if (passwordKey.equals(false)) { //check validity of password to avoid late data retrieval
                                            Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                else {
                                    if (userKey.equals(false)) { //check validity of user to avoid late data retrieval
                                        Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            catch(Exception e) {
                                System.out.println("Error: " + e);
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

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(LogIn.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    public static String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(LogIn.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;

    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(LogIn.KEY.getBytes(),LogIn.ALGORITHM);
        return key;
    }

}