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
import android.widget.TextView;

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
            setTheme(R.style.SettingsDark);
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

        TextView tip1 = findViewById(R.id.tipsTitle1);
        TextView tip2 = findViewById(R.id.tipsTitle2);
        TextView tip3 = findViewById(R.id.tipsTitle3);
        TextView tip4 = findViewById(R.id.tipsTitle4);
        TextView tip5 = findViewById(R.id.tipsTitle5);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tips");
        Query query = databaseReference.orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    tip1.setText(snapshot.child("0/tipsTitle").getValue().toString());
                    tip2.setText(snapshot.child("1/tipsTitle").getValue().toString());
                    tip3.setText(snapshot.child("2/tipsTitle").getValue().toString());
                    tip4.setText(snapshot.child("3/tipsTitle").getValue().toString());
                    tip5.setText(snapshot.child("4/tipsTitle").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error);
            }
        });

        Intent intent = new Intent(Tips.this, TipsItem.class);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query1 = databaseReference.child("0").orderByKey();
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Query query2 = databaseReference.child("1").orderByKey();
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Query query3 = databaseReference.child("2").orderByKey();
                query3.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Query query4 = databaseReference.child("3").orderByKey();
                query4.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Query query5 = databaseReference.child("4").orderByKey();
                query5.addListenerForSingleValueEvent(new ValueEventListener() {
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