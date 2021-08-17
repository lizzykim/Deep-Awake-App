package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ReportDetailActivity extends AppCompatActivity {

    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportdetailactivity);

        date = (TextView)findViewById(R.id.date);



    }
}