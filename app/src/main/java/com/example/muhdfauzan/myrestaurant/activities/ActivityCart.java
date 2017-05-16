package com.example.muhdfauzan.myrestaurant.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhdfauzan.myrestaurant.R;
import com.example.muhdfauzan.myrestaurant.config.DataManager;

/**
 * Created by MuhdFauzan on 5/17/2017.
 */

public class ActivityCart extends AppCompatActivity {

    private static final String LOG_TAG = ActivityCart.class.getSimpleName();
    private TextView tv_menu_list,tv_price_list, tv_total_price;
    private Button btn_clear, btn_reserve;
    private float totalPrice = 0;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle("My Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));


        initialize();


    }

    public void initialize() {
        tv_menu_list = (TextView)findViewById(R.id.tv_menu_list);
        tv_price_list = (TextView)findViewById(R.id.tv_price_list);
        tv_total_price = (TextView)findViewById(R.id.tv_total_price);
        btn_clear = (Button)findViewById(R.id.btn_clear);
        btn_reserve = (Button)findViewById(R.id.btn_reserve);



        String listItem = null;
        String listPrice = null;
        SharedPreferences sharedpreferences = getSharedPreferences(DataManager.PREF_CHART, Context.MODE_PRIVATE);

        int total = sharedpreferences.getInt("total", 0);

        for (int i = 0; i <total ; i++) {
            String name = sharedpreferences.getString("name_" + i, null);
            Float price = sharedpreferences.getFloat("price_" + i, 0);
            Log.d("Menu List:", "Total: " + total + "|" + "Name: " + name + " | " + "Price: " + price);
            totalPrice = totalPrice + price;
            if (listItem == null && listPrice == null) {
                listItem = name;
                listPrice = String.valueOf(price);
            }
            else {
                listItem = listItem + "\n" + name;
                listPrice= listPrice + "\n" + String.valueOf(price);
            }
            Log.d("Total Price:", "Total Price:" + totalPrice);
            tv_menu_list.setText(listItem);
            tv_price_list.setText(listPrice);
            tv_total_price.setText(String.valueOf(totalPrice));

        }

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCart();
            }
        });

        btn_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalPrice > 0) {
                    Intent i = new Intent(ActivityCart.this, ActivityReservation.class);
                    startActivity(i);
                }
                else
                    Toast.makeText(ActivityCart.this, "Cart is empty", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void clearCart(){
        SharedPreferences sharedpreferences = getSharedPreferences(DataManager.PREF_CHART, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear().commit();
        tv_menu_list.setText("");
        tv_price_list.setText("");
        tv_total_price.setText("");

    }

    private void totalPrice(){
        SharedPreferences sharedpreferences = getSharedPreferences(DataManager.PREF_CHART, Context.MODE_PRIVATE);

        int total = sharedpreferences.getInt("total", 0);
        for (int i = 0; i <total ; i++) {
            String name = sharedpreferences.getString("name_" + i, null);
            Float price = sharedpreferences.getFloat("price_" + i, 0);
            Log.d("Menu List:", "Total: " + total + "|" + "Name: " + name + " | " + "Price: " + price);
            totalPrice = totalPrice + price;
            tv_total_price.setText(String.valueOf(totalPrice));
        }
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}