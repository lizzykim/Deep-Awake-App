package com.example.semicolonapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class splash1 extends AppCompatActivity {

    public final static String TAG = "Splash1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();   //3 초 후 이미지를 닫아버림
                startActivity(new Intent(splash1.this, splash2.class));
            }
        },3000);

    }
}