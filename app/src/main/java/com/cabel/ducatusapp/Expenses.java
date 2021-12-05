package com.cabel.ducatusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Expenses extends AppCompatActivity {
    private SharedPrefs sharedPrefs;
    private TextView tvTotalAmount;
    private TextView txtDate;
    private LinearLayout layoutItems;
    private float totalAmount = 0;
    private String uid = "";

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
        setContentView(R.layout.activity_expenses);

        if(SharedPrefs.getCurrentUserId(Expenses.this) == null) {
            startActivity(new Intent(Expenses.this, LogIn.class));
        }

        //Display the current month and year
        txtDate = (TextView) findViewById(R.id.txtDate);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM yyyy");
        String m = month_date.format(cal.getTime());
        txtDate.setText(m);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        layoutItems = findViewById(R.id.layoutItems);
        uid = SharedPrefs.getCurrentUserId(Expenses.this);

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
                Intent intent = new Intent(Expenses.this, Home.class);
                startActivity(intent);
            }
        });

        //Budgeted icon intent
        Button btnBudgeted = findViewById(R.id.btnBudgeted);
        btnBudgeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Expenses.this, Budget.class);
                startActivity(intent);
            }
        });

        //Tips icon intent
        Button btnTips = findViewById(R.id.btnTips);
        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Expenses.this, Tips.class);
                startActivity(intent);
            }
        });

        //Add icon intent
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Expenses.this, ExpensesItem.class);
                startActivity(intent);
            }
        });
    }

    public float convertDpToPixel(float dp){
        Context context = Expenses.this;
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void displayItems(String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("expenses");
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

                            if(splitDbDate[2].contains(splitTextDate[1])) {
                                if(splitDbDate[0].contains(getMonthNumberFormat(splitTextDate[0]))) {
                                    String itemID = child.child("itemID").getValue().toString();
                                    String category = child.child("category").getValue().toString();
                                    String description = child.child("description").getValue().toString();
                                    float amount = Float.parseFloat(child.child("amount").getValue().toString());
                                    String date = child.child("date").getValue().toString();
                                    totalAmount += amount;

                                    tvTotalAmount.setText(String.format("₱%.2f", totalAmount));

                                    CardView cardView = new CardView(Expenses.this);
                                    cardView.setCardElevation(0);
                                    CardView.LayoutParams cardParam = new CardView.LayoutParams(
                                            925,
                                            (int)(convertDpToPixel(50))//144//64 * 2.25
                                    );
                                    cardParam.setMargins(0, (int) convertDpToPixel(10), 0, 0);
                                    cardView.setLayoutParams(cardParam);
                                    cardView.setId(Integer.parseInt(itemID));

                                    layoutItems.addView(cardView);

                                    RelativeLayout rl = new RelativeLayout(Expenses.this);
                                    RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            RelativeLayout.LayoutParams.MATCH_PARENT
                                    );
                                    rl.setLayoutParams(rlParam);
                                    rl.setId(R.id.rl);
                                    cardView.addView(rl);

                                    TextView tvcategory = new TextView(Expenses.this);
                                    RelativeLayout.LayoutParams categoryParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    categoryParam.setMargins((int) convertDpToPixel(10), (int) convertDpToPixel(5), 0, 0);
                                    tvcategory.setLayoutParams(categoryParam);
                                    tvcategory.setText(category);
                                    tvcategory.setTextColor(Color.parseColor("#5E5B5B"));
                                    rl.addView(tvcategory);

                                    TextView tvdescription = new TextView(Expenses.this);
                                    RelativeLayout.LayoutParams descriptionParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    descriptionParam.setMargins((int) convertDpToPixel(10), (int) convertDpToPixel(25), 0, 0);
                                    tvdescription.setLayoutParams(descriptionParam);
                                    tvdescription.setText(description);
                                    tvdescription.setTextColor(Color.parseColor("#5E5B5B"));
                                    rl.addView(tvdescription);

                                    TextView tvamount = new TextView(Expenses.this);
                                    RelativeLayout.LayoutParams amountParam = new RelativeLayout.LayoutParams((int) convertDpToPixel(70), ViewGroup.LayoutParams.WRAP_CONTENT);
                                    amountParam.setMargins((int) convertDpToPixel(146), (int) convertDpToPixel(5), 0, 0);
                                    tvamount.setLayoutParams(amountParam);
                                    tvamount.setText(String.format("₱%.2f", amount));
                                    tvamount.setTextColor(Color.parseColor("#5E5B5B"));
                                    tvamount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                    rl.addView(tvamount);

                                    TextView tvdate = new TextView(Expenses.this);
                                    RelativeLayout.LayoutParams dateParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dateParam.setMargins((int) convertDpToPixel(270), (int) convertDpToPixel(5), 0, 0);
                                    tvdate.setLayoutParams(dateParam);
                                    tvdate.setText(date);
                                    tvdate.setTextColor(Color.parseColor("#5E5B5B"));
                                    tvdate.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                    rl.addView(tvdate);

                                    if(sharedPrefs.loadDarkModeTheme()) {
                                        cardView.setCardBackgroundColor(Color.parseColor("#212121"));
                                        tvcategory.setTextColor(Color.parseColor("#F9F3F3"));
                                        tvdescription.setTextColor(Color.parseColor("#F9F3F3"));
                                        tvamount.setTextColor(Color.parseColor("#F9F3F3"));
                                        tvdate.setTextColor(Color.parseColor("#F9F3F3"));
                                    }
                                    else {
                                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                        tvcategory.setTextColor(Color.parseColor("#5E5B5B"));
                                        tvdescription.setTextColor(Color.parseColor("#5E5B5B"));
                                        tvamount.setTextColor(Color.parseColor("#5E5B5B"));
                                        tvdate.setTextColor(Color.parseColor("#5E5B5B"));
                                    }
                                }
                                else {
                                    tvTotalAmount.setText(String.format("₱%.2f", 0.00));
                                    layoutItems.removeAllViews();
                                }
                            }
                            else {
                                tvTotalAmount.setText(String.format("₱%.2f", 0.00));
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
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(Expenses.this,
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        String newDate = getMonthFormat(selectedMonth) + " " + selectedYear;
                        txtDate.setText(newDate);
                        displayItems(newDate);
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