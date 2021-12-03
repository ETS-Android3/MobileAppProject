package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Tips extends AppCompatActivity {
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
        setContentView(R.layout.activity_tips);

        if(SharedPrefs.getCurrentUserId(Tips.this) == null) {
            startActivity(new Intent(Tips.this, LogIn.class));
        }

        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        CardView card4 = findViewById(R.id.card4);
        CardView card5 = findViewById(R.id.card5);

        Intent intent = new Intent(Tips.this, TipsItem.class);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tips/0");
                Query query = databaseReference.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String title = snapshot.child("tipsTitle").getValue().toString();
                            String message = snapshot.child("tipsContent").getValue().toString();
                            intent.putExtra("title", title);
                            intent.putExtra("image_url",R.drawable.tips1);
                            intent.putExtra("message", message);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Error: " + error);
                    }
                });

            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tips/1");
                Query query = databaseReference.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String title = snapshot.child("tipsTitle").getValue().toString();
                            String message = snapshot.child("tipsContent").getValue().toString();
                            intent.putExtra("title", title);
                            intent.putExtra("image_url",R.drawable.tips2);
                            intent.putExtra("message", message);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Error: " + error);
                    }
                });
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tips/2");
                Query query = databaseReference.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String title = snapshot.child("tipsTitle").getValue().toString();
                            String message = snapshot.child("tipsContent").getValue().toString();
                            intent.putExtra("title", title);
                            intent.putExtra("image_url",R.drawable.tips3);
                            intent.putExtra("message", message);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Error: " + error);
                    }
                });
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tips/3");
                Query query = databaseReference.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String title = snapshot.child("tipsTitle").getValue().toString();
                            String message = snapshot.child("tipsContent").getValue().toString();
                            intent.putExtra("title", title);
                            intent.putExtra("image_url",R.drawable.tips4);
                            intent.putExtra("message", message);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Error: " + error);
                    }
                });
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tips/4");
                Query query = databaseReference.orderByKey();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String title = snapshot.child("tipsTitle").getValue().toString();
                            String message = snapshot.child("tipsContent").getValue().toString();
                            intent.putExtra("title", title);
                            intent.putExtra("image_url",R.drawable.tips5);
                            intent.putExtra("message", message);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Error: " + error);
                    }
                });
            }
        });

        //Home icon intent
        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tips.this, Home.class);
                startActivity(intent);
            }
        });

        //Budgeted icon intent
        Button btnBudgeted = findViewById(R.id.btnBudgeted);
        btnBudgeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tips.this, Budget.class);
                startActivity(intent);
            }
        });

        //Expenses icon intent
        Button btnExpenses = findViewById(R.id.btnExpenses);
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tips.this, Expenses.class);
                startActivity(intent);
            }
        });
    }
}