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
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Budget extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private LinearLayout layoutItems;
    private TextView txtDate;
    private TextView tvTotalBudget;
    private TextView tvTotalActivity;
    private TextView tvTotalAvailable;
    private float totalBudget = 0;
    private float totalActivity = 0;
    private float totalAvailable = 0;
    private String uid = "";

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat month = new SimpleDateFormat("MMMM");
    String initialMonth = month.format(cal.getTime());

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
        setContentView(R.layout.activity_budget);

        if(SharedPrefs.getCurrentUserId(Budget.this) == null) {
            startActivity(new Intent(Budget.this, LogIn.class));
        }

        //Display the current month and year
        txtDate = findViewById(R.id.txtDate);
        cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM yyyy");
        String m = month_date.format(cal.getTime());

        txtDate.setText(m);

        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvTotalActivity = findViewById(R.id.tvTotalActivity);
        tvTotalAvailable = findViewById(R.id.tvTotalAvailable);

        layoutItems = findViewById(R.id.layoutItems);
        uid = SharedPrefs.getCurrentUserId(Budget.this);

        displayItems(m);

        Button btnSetDate = findViewById(R.id.btnDate);
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDate(btnSetDate);
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

    public void displayItems(String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("budget");
        Query queryAll = databaseReference.orderByKey();
        queryAll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child: snapshot.getChildren()) {
                    if (snapshot.exists()) { //check if firebase is empty
                        if(child.child("userID").getValue().toString().equals(uid)) { //retrieve all items related to current user
                            String dbDate = child.child("date").getValue().toString();
                            String[] splitDbDate = dbDate.split("/", 0); //index 0 = month; index 1 = day; index 2 = year
                            String[] splitTextDate = date.split(" ", 0); //index 0 = month; index 1 = year

                            if(splitDbDate[2].contains(splitTextDate[1])) { //if item's date is same as selected date
                                if(splitDbDate[0].contains(getMonthNumberFormat(splitTextDate[0]))) { //if item's month is same as selected date
                                    String itemID = child.child("itemID").getValue().toString();
                                    String category = child.child("category").getValue().toString();
                                    String description = child.child("description").getValue().toString();
                                    float budget = Float.parseFloat(child.child("budget").getValue().toString());
                                    float activity = Float.parseFloat(child.child("activity").getValue().toString());
                                    float available = Float.parseFloat(child.child("available").getValue().toString());

                                    totalBudget += budget;
                                    totalActivity += activity;
                                    totalAvailable += available;

                                    tvTotalBudget.setText(String.format("₱%.2f", totalBudget));
                                    tvTotalActivity.setText(String.format("₱%.2f", totalActivity));
                                    tvTotalAvailable.setText(String.format("₱%.2f", totalAvailable));

                                    CardView cardView = new CardView(Budget.this);
                                    CardView.LayoutParams cardParam = new CardView.LayoutParams(
                                            925,
                                            (int)(convertDpToPixel(50))//144//64 * 2.25
                                    );
                                    cardParam.setMargins(0, (int) convertDpToPixel(10), 0, 0);
                                    cardView.setLayoutParams(cardParam);
                                    cardView.setCardElevation(0);
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
                                    //rl.setBackgroundColor(Color.parseColor("#7C9885"));
                                    rl.setId(R.id.rl);
                                    cardView.addView(rl);

                                    TextView tvcategory = new TextView(Budget.this);
                                    RelativeLayout.LayoutParams categoryParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    categoryParam.setMargins((int) convertDpToPixel(10), (int) convertDpToPixel(5), 0, 0);
                                    tvcategory.setLayoutParams(categoryParam);
                                    tvcategory.setText(category);
                                    rl.addView(tvcategory);

                                    TextView tvdescription = new TextView(Budget.this);
                                    RelativeLayout.LayoutParams descriptionParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    descriptionParam.setMargins((int) convertDpToPixel(10), (int) convertDpToPixel(25), 0, 0);
                                    tvdescription.setLayoutParams(descriptionParam);
                                    tvdescription.setText(description);
                                    rl.addView(tvdescription);

                                    TextView tvbudget = new TextView(Budget.this);
                                    RelativeLayout.LayoutParams budgetParam = new RelativeLayout.LayoutParams((int) convertDpToPixel(70), ViewGroup.LayoutParams.WRAP_CONTENT);
                                    budgetParam.setMargins((int) convertDpToPixel(105), (int) convertDpToPixel(5), 0, 0);
                                    tvbudget.setLayoutParams(budgetParam);
                                    tvbudget.setText(String.format("₱%.2f", budget));
                                    tvbudget.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                    rl.addView(tvbudget);

                                    TextView tvactivity = new TextView(Budget.this);
                                    RelativeLayout.LayoutParams activityParam = new RelativeLayout.LayoutParams((int) convertDpToPixel(70), ViewGroup.LayoutParams.WRAP_CONTENT);
                                    activityParam.setMargins((int) convertDpToPixel(184), (int) convertDpToPixel(5), 0, 0);
                                    tvactivity.setLayoutParams(activityParam);
                                    tvactivity.setText(String.format("₱%.2f", activity));
                                    tvactivity.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                    rl.addView(tvactivity);

                                    TextView tvavailable = new TextView(Budget.this);
                                    RelativeLayout.LayoutParams availableParam = new RelativeLayout.LayoutParams((int) convertDpToPixel(70), ViewGroup.LayoutParams.WRAP_CONTENT);
                                    availableParam.setMargins((int) convertDpToPixel(276), (int) convertDpToPixel(5), 0, 0);
                                    tvavailable.setLayoutParams(availableParam);
                                    tvavailable.setText(String.format("₱%.2f", available));
                                    tvavailable.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                    rl.addView(tvavailable);

                                    if(sharedPrefs.loadDarkModeTheme()) {
                                        cardView.setCardBackgroundColor(Color.parseColor("#212121"));
                                        tvcategory.setTextColor(Color.parseColor("#F9F3F3"));
                                        tvdescription.setTextColor(Color.parseColor("#F9F3F3"));
                                        tvbudget.setTextColor(Color.parseColor("#F9F3F3"));
                                        tvactivity.setTextColor(Color.parseColor("#F9F3F3"));
                                        tvavailable.setTextColor(Color.parseColor("#F9F3F3"));
                                    }
                                    else {
                                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                        tvcategory.setTextColor(Color.parseColor("#5E5B5B"));
                                        tvdescription.setTextColor(Color.parseColor("#5E5B5B"));
                                        tvbudget.setTextColor(Color.parseColor("#5E5B5B"));
                                        tvactivity.setTextColor(Color.parseColor("#5E5B5B"));
                                        tvavailable.setTextColor(Color.parseColor("#5E5B5B"));
                                    }
                                }
                                else {

                                    totalBudget = 0;
                                    totalActivity = 0;
                                    totalAvailable = 0;
                                    tvTotalBudget.setText(String.format("₱%.2f", 0.00));
                                    tvTotalActivity.setText(String.format("₱%.2f", 0.00));
                                    tvTotalAvailable.setText(String.format("₱%.2f", 0.00));
                                    layoutItems.removeAllViews();
                                }
                            }
                            else {
                                totalBudget = 0;
                                totalActivity = 0;
                                totalAvailable = 0;
                                tvTotalBudget.setText(String.format("₱%.2f", 0.00));
                                tvTotalActivity.setText(String.format("₱%.2f", 0.00));
                                tvTotalAvailable.setText(String.format("₱%.2f", 0.00));
                                layoutItems.removeAllViews();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void btnDate(View view) {
        final Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(Budget.this,
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        String newDate = getMonthFormat(selectedMonth) + " " + selectedYear;
                        txtDate.setText(newDate);

                        if(!getMonthFormat(selectedMonth).equals(initialMonth)) {
                            initialMonth = "";
                            displayItems(newDate);
                        }
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(Calendar.JANUARY)
                .setMinYear(1990)
                .setActivatedYear(today.get(Calendar.YEAR))
                .setMaxYear(2030)
                .setTitle("Select month and year")
                .build().show();
    }

    public String getMonthFormat(int month) {
        if(month == 0) {
            return "January";
        }
        if(month == 1) {
            return "February";
        }
        if(month == 2) {
            return "March";
        }
        if(month == 3) {
            return "April";
        }
        if(month == 4) {
            return "May";
        }
        if(month == 5) {
            return "June";
        }
        if(month == 6) {
            return "July";
        }
        if(month == 7) {
            return "August";
        }
        if(month == 8) {
            return "September";
        }
        if(month == 9) {
            return "October";
        }
        if(month == 10) {
            return "November";
        }
        if(month == 11) {
            return "December";
        }

        return "January";
    }

    public String getMonthNumberFormat(String month) {
        if(month.equals("January")) {
            return "01";
        }
        if(month.equals("February")) {
            return "02";
        }
        if(month.equals("March")) {
            return "03";
        }
        if(month.equals("April")) {
            return "04";
        }
        if(month.equals("May")) {
            return "05";
        }
        if(month.equals("June")) {
            return "06";
        }
        if(month.equals("July")) {
            return "07";
        }
        if(month.equals("August")) {
            return "08";
        }
        if(month.equals("September")) {
            return "09";
        }
        if(month.equals("October")) {
            return "10";
        }
        if(month.equals("November")) {
            return "11";
        }
        if(month.equals("December")) {
            return "12";
        }

        return "01";
    }
}