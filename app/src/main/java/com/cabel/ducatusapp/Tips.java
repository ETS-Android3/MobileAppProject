package com.cabel.ducatusapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

        RelativeLayout rcard1 = findViewById(R.id.rcard1);
        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        CardView card4 = findViewById(R.id.card4);
        CardView card5 = findViewById(R.id.card5);

        Intent intent = new Intent(Tips.this, TipsItem.class);

        ImageView imgTips = findViewById(R.id.imgTips);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Be organized";
                String message = "Having money comes with a great spending power! " +
                        "It is important to be organized and be knowledgeable of your needs. " +
                        "When it comes to personal spending, you should know what you have and " +
                        "what you need. Knowing them would greatly help with controlling your " +
                        "spending as you no longer buy things you want and buy what's only " +
                        "missing on your shelves. Checking your current inventory before going " +
                        "shopping would be of great help to make sure you only buy things that " +
                        "you need.";
                intent.putExtra("title", title);
                intent.putExtra("image_url",R.drawable.tips1);
                intent.putExtra("message", message);
                startActivity(intent);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Don't eat out";
                String message = "Ordering food and going out can be extremely tempting! " +
                        "If you look at it however, it drains out your wallet quickly. " +
                        "Don't do it everyday as you won't have savings left by the end " +
                        "of the month. Try to cook your own meals, this way you can save up " +
                        "on money and you would even gain a new skill (It's a two-for-one " +
                        "if you ask us!). Buying ingredients for a meal would be far cheaper " +
                        "than buying out food, you can even get more than one meal from a " +
                        "single buy.";
                intent.putExtra("title", title);
                intent.putExtra("image_url",R.drawable.tips2);
                intent.putExtra("message", message);
                startActivity(intent);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Lessen your energy consumption";
                String message = "Turn off anything in your home when it is not in use. One " +
                        "of the monthly expenses we have are our electricity bill, and it " +
                        "sometimes have us by surprise. In the summer, we tend to use our air " +
                        "conditioners more often than usual. You should try to lessen that " +
                        "and use our appliances only when we need them. Unplug them when they " +
                        "are not in use!";
                intent.putExtra("title", title);
                intent.putExtra("image_url",R.drawable.tips3);
                intent.putExtra("message", message);
                startActivity(intent);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Stay at home";
                String message = "Staying at home can be fun! You don't have to go out to have " +
                        "a fun day and relax. Light some scented candles and cook a special " +
                        "meal; decorate for the gods! A night alone or even a night with " +
                        "friends shouldn't be expensive. Watch a movie and pamper yourselves " +
                        "in the comforts of your very own home.";
                intent.putExtra("title", title);
                intent.putExtra("image_url",R.drawable.tips4);
                intent.putExtra("message", message);
                startActivity(intent);
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Track your expenses";
                String message = "Well, this is the sole purpose of this app! Tracking your " +
                        "expenses is highly beneficial as you can see the money that goes out " +
                        "from your pockets. You can know how much you're spending in every aspect " +
                        "of your needs. With this, you can realize where you are spending too much " +
                        "on, and maybe have awareness on what expenditures you can completely " +
                        "stop spending on! You get to learn your spending habits and know what " +
                        "you can improve on. ";
                intent.putExtra("title", title);
                intent.putExtra("image_url",R.drawable.tips5);
                intent.putExtra("message", message);
                startActivity(intent);
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