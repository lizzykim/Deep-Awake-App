package com.example.semicolonapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.semicolonapp.data.DataHolder;
import com.example.semicolonapp.data.GetNameResponse;
import com.example.semicolonapp.network.RetrofitClient;
import com.example.semicolonapp.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

    private ImageButton Home,Report, Map, Music, Setting;
    TextView textView5,userinfo;
    private ServiceApi service;//retrofit 관련



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
        textView5 = (TextView)findViewById(R.id.textView5);
        userinfo = (TextView)findViewById(R.id.textView11);

        service = RetrofitClient.getClient().create(ServiceApi.class);

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
                        Intent intent4 = new Intent(SettingActivity.this, AlarmActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.Setting:
//                        Intent intent5 = new Intent(SettingActivity.this, SettingActivity.class);
//                        startActivity(intent5);
//                        break;

                    case R.id.textView11: //회원 정보 확인
                        Intent intent5 = new Intent(SettingActivity.this,UserInfoActivity.class);
                        startActivity(intent5);
                        break;

                }
            }
        };

        Home.setOnClickListener(onClickListener);
        Report.setOnClickListener(onClickListener);
        Map.setOnClickListener(onClickListener);
        Music.setOnClickListener(onClickListener);
        //Setting.setOnClickListener(onClickListener);

        userinfo.setOnClickListener(onClickListener);

        showUserName(DataHolder.getUseremail()); //사용자 이름을 알아내기 위해 로그인한 이메일을 대입.

    }

    //RETROFIT 2이용해서  이메일 이용하여 사용자이름 받아노는 메소드
    private void showUserName(String data) {
        service.getName(data).enqueue(new Callback<GetNameResponse>() {
            @Override
            public void onResponse(Call<GetNameResponse> call, Response<GetNameResponse> response) {
                GetNameResponse result = response.body();
                Log.d("message", ""+result.getMessage());

                if(result.getCode() == 200){
                    //~님 환영합니다 라고 써주기
                    textView5.setText(result.getUsername()+"님");
                    Log.d("Username", ""+result.getUsername());
                }

            }

            @Override
            public void onFailure(Call<GetNameResponse> call, Throwable t) {
                Log.e("사용자 이름 가져오기 에러 발생", t.getMessage());
                t.printStackTrace();
            }
        });

    }
}