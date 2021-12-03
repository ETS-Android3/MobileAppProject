package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Budget extends AppCompatActivity {
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
        setContentView(R.layout.activity_budget);

        if(SharedPrefs.getCurrentUserId(Budget.this) == null) {
            startActivity(new Intent(Budget.this, LogIn.class));
        }

        //Display the current month and year
        TextView txtDate = (TextView) findViewById(R.id.txtDate);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM yyyy");
        String m = month_date.format(cal.getTime());
        txtDate.setText(m);

        LinearLayout layoutItems = findViewById(R.id.layoutItems);
        String uid = SharedPrefs.getCurrentUserId(Budget.this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("budget");
        Query queryAll = databaseReference.orderByKey();
        queryAll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child: snapshot.getChildren()) {
                    if (snapshot.exists()) { //check if firebase is empty
                        if(child.child("userID").getValue().toString().equals(uid)) { //retrieve all items related to current user
                            String itemID = child.child("itemID").getValue().toString();
                            String category = child.child("category").getValue().toString();
                            String description = child.child("description").getValue().toString();
                            float budget = Float.parseFloat(child.child("budget").getValue().toString());
                            float activity = Float.parseFloat(child.child("activity").getValue().toString());
                            float available = Float.parseFloat(child.child("available").getValue().toString());

                            CardView cardView = new CardView(Budget.this);
                            CardView.LayoutParams cardParam = new CardView.LayoutParams(
                                    925,
                                    (int)(convertDpToPixel(50))//144//64 * 2.25
                            );
                            cardParam.setMargins(0, (int) convertDpToPixel(10), 0, 0);
                            cardView.setLayoutParams(cardParam);
                            cardView.setId(Integer.parseInt(itemID));
                            cardView.setOnClickListener(new View.OnClickListener() { //set on click listener for each cardview
                                @Override
                                public void onClick(View view) {
                                    String id = String.valueOf(view.getId());
                                    if(child.child("itemID").getValue().toString().equals(id)) {
                                        Intent intent = new Intent(getApplicationContext(), EditBudgetItem.class);
                                        String dbCategory = child.child("category").getValue().toString();
                                        String dbDescription = child.child("description").getValue().toString();
                                        float dbBudget = Float.parseFloat(child.child("budget").getValue().toString());
                                        float dbActivity = Float.parseFloat(child.child("activity").getValue().toString());
                                        float dbAvailable = Float.parseFloat(child.child("available").getValue().toString());

                                        intent.putExtra("itemID", id);
                                        intent.putExtra("category", dbCategory);
                                        intent.putExtra("description", dbDescription);
                                        intent.putExtra("budget", dbBudget);
                                        intent.putExtra("activity", dbActivity);
                                        intent.putExtra("available", dbAvailable);
                                        startActivity(intent);
                                    }
                                }
                            });
                            layoutItems.addView(cardView);

                            RelativeLayout rl = new RelativeLayout(Budget.this);
                            RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT
                            );
                            rl.setLayoutParams(rlParam);
                            rl.setId(R.id.rl);
                            cardView.addView(rl);

                            TextView tvcategory = new TextView(Budget.this);
                            RelativeLayout.LayoutParams categoryParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            categoryParam.setMargins((int) convertDpToPixel(10), (int) convertDpToPixel(5), 0, 0);
                            tvcategory.setLayoutParams(categoryParam);
                            tvcategory.setText(category);
                            tvcategory.setTextColor(Color.parseColor("#5E5B5B"));
                            rl.addView(tvcategory);

                            TextView tvbudget = new TextView(Budget.this);
                            RelativeLayout.LayoutParams budgetParam = new RelativeLayout.LayoutParams((int) convertDpToPixel(70), ViewGroup.LayoutParams.WRAP_CONTENT);
                            budgetParam.setMargins((int) convertDpToPixel(110), (int) convertDpToPixel(5), 0, 0);
                            tvbudget.setLayoutParams(budgetParam);
                            tvbudget.setText(String.format("₱%.2f", budget));
                            tvbudget.setTextColor(Color.parseColor("#5E5B5B"));
                            tvbudget.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                            rl.addView(tvbudget);

                            TextView tvactivity = new TextView(Budget.this);
                            RelativeLayout.LayoutParams activityParam = new RelativeLayout.LayoutParams((int) convertDpToPixel(70), ViewGroup.LayoutParams.WRAP_CONTENT);
                            activityParam.setMargins((int) convertDpToPixel(183), (int) convertDpToPixel(5), 0, 0);
                            tvactivity.setLayoutParams(activityParam);
                            tvactivity.setText(String.format("₱%.2f", activity));
                            tvactivity.setTextColor(Color.parseColor("#5E5B5B"));
                            tvactivity.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                            rl.addView(tvactivity);

                            TextView tvavailable = new TextView(Budget.this);
                            RelativeLayout.LayoutParams availableParam = new RelativeLayout.LayoutParams((int) convertDpToPixel(70), ViewGroup.LayoutParams.WRAP_CONTENT);
                            availableParam.setMargins((int) convertDpToPixel(269), (int) convertDpToPixel(5), 0, 0);
                            tvavailable.setLayoutParams(availableParam);
                            tvavailable.setText(String.format("₱%.2f", available));
                            tvavailable.setTextColor(Color.parseColor("#5E5B5B"));
                            tvavailable.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                            rl.addView(tvavailable);

                            TextView tvdescription = new TextView(Budget.this);
                            RelativeLayout.LayoutParams descriptionParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            descriptionParam.setMargins((int) convertDpToPixel(10), (int) convertDpToPixel(25), 0, 0);
                            tvdescription.setLayoutParams(descriptionParam);
                            tvdescription.setText(description);
                            tvdescription.setTextColor(Color.parseColor("#5E5B5B"));
                            rl.addView(tvdescription);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Home icon intent
        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, Home.class);
                startActivity(intent);
            }
        });

        //Expenses icon intent
        Button btnExpenses = findViewById(R.id.btnExpenses);
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, Expenses.class);
                startActivity(intent);
            }
        });

        //Tips icon intent
        Button btnTips = findViewById(R.id.btnTips);
        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, Tips.class);
                startActivity(intent);
            }
        });

        //Add icon intent
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, BudgetItem.class);
                startActivity(intent);
            }
        });
    }

    public float convertDpToPixel(float dp){
        Context context = Budget.this;
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}