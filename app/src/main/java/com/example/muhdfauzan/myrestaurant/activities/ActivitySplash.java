package com.example.muhdfauzan.myrestaurant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.example.muhdfauzan.myrestaurant.R;

public class ActivitySplash extends AppCompatActivity{

    private static final String TAG = ActivitySplash.class.getSimpleName();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onFinish() {
                Intent intent = new Intent(getBaseContext(), ActivityMain.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();

    }


}

