package com.example.semicolonapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class splash2 extends AppCompatActivity {

    Button bt_splashNext2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash2);

        bt_splashNext2 = (Button)findViewById(R.id.bt_splashNext2);
        bt_splashNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(splash2.this, splash3.class);
                startActivity(intent);
                finish();
            }
        });


    }
}