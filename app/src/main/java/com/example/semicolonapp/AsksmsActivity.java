package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.semicolonapp.data.DataHolder;

public class AsksmsActivity extends AppCompatActivity {


    static final int SMS_SEND_PERMISSION = 2;
    Button gotoHome;
    Switch checksmssend;
    String isSendSMS = "false"; //(false = 안보냄)사용자에게 문자 보낼지 말지


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asksmsactivity);

        //권한이 부여되어 있는지 확인
        int permissonCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS);

        if (permissonCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "SMS 발신권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "SMS 발신권한 없음", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                //이곳에 권한이 왜 필요한지 설명하는 Toast 혹은 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(getApplicationContext(), "SMS 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSION);

            }
        }

        gotoHome = (Button)findViewById(R.id.buttonSend);
        checksmssend = (Switch)findViewById(R.id.selectsms);


        checksmssend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.getId() == R.id.selectsms){
                    if(isChecked){
                        Toast.makeText(getApplicationContext(), "문자 보냄", Toast.LENGTH_SHORT).show();
                        //mapactivity 에서 분간 할 수 있는 신호 만들어주기
                        DataHolder.setIsSendSMS("true");

                    }else{
                        Toast.makeText(getApplicationContext(), "문자 안보냄", Toast.LENGTH_SHORT).show();
                        DataHolder.setIsSendSMS("false");
                    }
                }
            }
        });

        gotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AsksmsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SMS_SEND_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "SMS 권한 승인함", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS 권한 거부함", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }









}