package com.example.semicolonapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semicolonapp.adapter.HelperAdapter;

import java.util.ArrayList;

/*참고링크
*https://www.youtube.com/watch?v=ZJepo2wRiBk (JSON 파일 parcing 하는 법)
* */
public class ServiceAreaInfoActivity extends AppCompatActivity {

    private Button back;


    RecyclerView recyclerView;
    ArrayList<String> name = new ArrayList<>();//휴게소명
    ArrayList<String> road_type = new ArrayList<>();//도로종류
    ArrayList<String> road_route_name = new ArrayList<>();//도로노선명
    ArrayList<Float> latitude = new ArrayList<>();//위도
    ArrayList<Float> longitude = new ArrayList<>();//경도
    ArrayList<String> gas_station = new ArrayList<>();//주유소 유무
    ArrayList<String> lpg_charge = new ArrayList<>();//LPG충전소유무
    ArrayList<String> electric_charge = new ArrayList<>();//전기차충전소유무
    ArrayList<String> phone = new ArrayList<>();//휴게소 전화번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_area_info_activity);

        recyclerView= findViewById(R.id.recyclerView);//recylcerview 참조
        back = (Button)findViewById(R.id.back); //뒤로가기 버튼 참조

        Intent intent = getIntent();// 이전 액티비티(MapActivity)에서 수신할 데이터를 받기 위해 선엄
        name = intent.getStringArrayListExtra("name");
        road_type= intent.getStringArrayListExtra("road_type");
        road_route_name= intent.getStringArrayListExtra("road_route_name");
        latitude = (ArrayList<Float>) intent.getSerializableExtra("latitude");
        longitude = (ArrayList<Float>) intent.getSerializableExtra("longitude");
        gas_station = intent.getStringArrayListExtra("gas_station");
        lpg_charge= intent.getStringArrayListExtra("lpg_charge");
        electric_charge= intent.getStringArrayListExtra("electric_charge");
        phone = intent.getStringArrayListExtra("phone");

//        Log.d("ServiceAreaInfoActivity", "name:"+ name);
//        Log.d("ServiceAreaInfoActivity", "road_type:"+ road_type);
//        Log.d("ServiceAreaInfoActivity", "road_route_name:"+ road_route_name);
//        Log.d("ServiceAreaInfoActivity", "latitude:"+ latitude);
//        Log.d("ServiceAreaInfoActivity", "longitude:"+ longitude);
//        Log.d("ServiceAreaInfoActivity", "gas_station:"+ gas_station);
//        Log.d("ServiceAreaInfoActivity", "lpg_charge:"+ lpg_charge);
//        Log.d("ServiceAreaInfoActivity", "electric_charge:"+ electric_charge);




        //뒤로가기
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceAreaInfoActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        HelperAdapter helperAdapter = new HelperAdapter(name,road_type,road_route_name,latitude,longitude,gas_station,lpg_charge,electric_charge,phone, ServiceAreaInfoActivity.this);//어뎁터 객체만들기
        recyclerView.setAdapter(helperAdapter);

    }

}