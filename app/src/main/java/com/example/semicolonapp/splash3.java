package com.example.semicolonapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class splash3 extends AppCompatActivity {

    Button bt_splashBefore3;
    Button bt_splashNext3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash3);

        bt_splashBefore3 = (Button)findViewById(R.id.bt_splashBefore3);
        bt_splashNext3= (Button)findViewById(R.id.bt_splashNext3);

        bt_splashBefore3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(splash3.this, splash2.class);
                startActivity(intent);
                finish();
            }
        });

        bt_splashNext3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(splash3.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}