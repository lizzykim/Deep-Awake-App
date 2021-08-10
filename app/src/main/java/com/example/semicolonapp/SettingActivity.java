package com.example.semicolonapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.semicolonapp.R;

public class SettingActivity extends AppCompatActivity {

    private ImageButton Home,Report, Map, Music, Setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingactivity);

        //액티비티 이동 버튼 관련 코드
        Home = (ImageButton)findViewById(R.id.Home);
        Report = (ImageButton)findViewById(R.id.Report);
        Map = (ImageButton)findViewById(R.id.Map);
        Music = (ImageButton)findViewById(R.id.Music);
        Setting = (ImageButton)findViewById(R.id.Setting);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.Home:
                        Intent intent1 = new Intent(SettingActivity.this, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.Report:
                        Intent intent6 = new Intent(SettingActivity.this, ReportActivity.class);
                        startActivity(intent6);
                        break;
                    case R.id.Map:
                        Intent intent2 = new Intent(SettingActivity.this, MapActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.Music:
                        Intent intent4 = new Intent(SettingActivity.this, SongActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.Setting:
//                        Intent intent5 = new Intent(SettingActivity.this, SettingActivity.class);
//                        startActivity(intent5);
//                        break;

                }
            }
        };

        Home.setOnClickListener(onClickListener);
        Report.setOnClickListener(onClickListener);
        Map.setOnClickListener(onClickListener);
        Music.setOnClickListener(onClickListener);
        //Setting.setOnClickListener(onClickListener);


    }
}