package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReportDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    String date,address,weather,temperature,humidity,pm10value,pm25value,so2value,covalue,o3value,no2value,pm10grade,pm25grade,so2grade,cograde,o3grade,no2grade;
    Double latitude,longitude;
    TextView tv_date,tv_location,tv_weather,tv_temperature,tv_humidity;
    TextView tv_pm10value,tv_pm25value,tv_so2value,tv_covalue,tv_o3value,tv_no2value,tv_pm10grade,tv_pm25grade,tv_so2grade,tv_cograde,tv_o3grade,tv_no2grade;
    ImageView icon_pm10grade, icon_pm25grade, icon_so2grade, icon_cograde,icon_o3grade, icon_no2grade;
    GoogleMap map;
    Location location;

    private ImageButton Home,Report, Map, Music, Setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportdetailactivity);
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
                        Intent intent1 = new Intent(ReportDetailActivity.this, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.Map:
                        Intent intent2 = new Intent(ReportDetailActivity.this, MapActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.Music:
                        Intent intent3 = new Intent(ReportDetailActivity.this, AlarmActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.Setting:
                        Intent intent4 = new Intent(ReportDetailActivity.this, SettingActivity.class);
                        startActivity(intent4);
                        break;
                }
            }
        };

        Home.setOnClickListener(onClickListener);
        Map.setOnClickListener(onClickListener);
        Music.setOnClickListener(onClickListener);
        Setting.setOnClickListener(onClickListener);
        init();
    }

    void init(){
        //xml 주소 참조
        tv_date = (TextView)findViewById(R.id.date);
        tv_location = (TextView)findViewById(R.id.location);
        tv_weather= (TextView)findViewById(R.id.weather);
        tv_temperature= (TextView)findViewById(R.id.temperature);
        tv_humidity= (TextView)findViewById(R.id.humidity);

        tv_pm10value = (TextView)findViewById(R.id.pm10Value);
        tv_pm25value = (TextView)findViewById(R.id.pm25Value);
        tv_so2value= (TextView)findViewById(R.id.so2Value);
        tv_covalue= (TextView)findViewById(R.id.coValue);
        tv_o3value= (TextView)findViewById(R.id.o3Value);
        tv_no2value = (TextView)findViewById(R.id.no2Value);
//        tv_pm10grade = (TextView)findViewById(R.id.pm10Grade);
//        tv_pm25grade= (TextView)findViewById(R.id.pm25Grade);
//        tv_so2grade= (TextView)findViewById(R.id.so2Grade);
//        tv_cograde= (TextView)findViewById(R.id.coGrade);
//        tv_o3grade= (TextView)findViewById(R.id.o3Grade);
//        tv_no2grade= (TextView)findViewById(R.id.no2Grade);

        icon_pm10grade = (ImageView)findViewById(R.id.icon_pm10);
        icon_pm25grade = (ImageView)findViewById(R.id.icon_pm25);
        icon_so2grade = (ImageView)findViewById(R.id.icon_so2);
        icon_cograde = (ImageView)findViewById(R.id.icon_co);
        icon_o3grade = (ImageView)findViewById(R.id.icon_o3);
        icon_no2grade = (ImageView)findViewById(R.id.icon_no2);

        //intent로 data전달 받기
        date = getIntent().getStringExtra("date");
        address = getIntent().getStringExtra("address");
        weather = getIntent().getStringExtra("weather");
        temperature = getIntent().getStringExtra("temperature");
        humidity = getIntent().getStringExtra("humidity");

        latitude =  Double.valueOf(getIntent().getStringExtra("lat"));
        longitude = Double.valueOf(getIntent().getStringExtra("long"));

        pm10value = getIntent().getStringExtra("pm10Value");
        pm25value= getIntent().getStringExtra("pm25Value");
        so2value = getIntent().getStringExtra("so2Value");
        covalue = getIntent().getStringExtra("coValue");
        o3value = getIntent().getStringExtra("o3Value");
        no2value = getIntent().getStringExtra("no2Value");
        pm10grade = getIntent().getStringExtra("pm10Grade");
        pm25grade = getIntent().getStringExtra("pm25Grade");
        so2grade = getIntent().getStringExtra("so2Grade");
        cograde = getIntent().getStringExtra("coGrade");
        o3grade = getIntent().getStringExtra("o3Grade");
        no2grade = getIntent().getStringExtra("no2Grade");

        Log.i("위도",latitude+","+longitude);

        //뷰에 뿌려주기
        tv_date.setText(date);
        tv_location.setText(address);
        tv_weather.setText(weather);
        tv_temperature.setText(temperature +"ºC");
        tv_humidity.setText(humidity+"%");

        tv_pm10value.setText("미세먼지 "+pm10value +"㎍/㎥");
        tv_pm25value.setText("초미세먼지 "+pm25value +"㎍/㎥");
        tv_so2value.setText("이황산가스 "+so2value+"ppm");
        tv_covalue.setText("일산화탄소 "+covalue+"ppm");
        tv_o3value.setText("오존 "+o3value+"ppm");
        tv_no2value.setText("이산화질소 "+no2value+"ppm");

//        String [] list = new String[]{pm10grade,pm25grade,so2grade,cograde,o3grade,no2grade};
//        for (int i=0 ; i<list.length; i++){
//            switch (list[i]){
//                case "1":
//                    list[i] ="좋음";
//                    break;
//                case "2":
//                    list[i] ="보통";
//                    break;
//                case "3":
//                    list[i] ="나쁨";
//                    break;
//                case "4":
//                    list[i] ="매우 나쁨";
//                    break;
//            }
//        }

        switch (pm10grade){
                case "1":
                    pm10grade ="좋음";
                    icon_pm10grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_smile));
                    break;
                case "2":
                    pm10grade ="보통";
                    icon_pm10grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_soso));
                    break;
                case "3":
                    pm10grade ="나쁨";
                    icon_pm10grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_bad));
                    break;
                case "4":
                    pm10grade ="매우 나쁨";
                    icon_pm10grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_cry));
                    break;
            }

        switch (pm25grade){
            case "1":
                pm25grade ="좋음";
                icon_pm25grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_smile));
                break;
            case "2":
                pm25grade ="보통";
                icon_pm25grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_soso));
                break;
            case "3":
                pm25grade ="나쁨";
                icon_pm25grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_bad));
                break;
            case "4":
                pm25grade ="매우 나쁨";
                icon_pm25grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_cry));
                break;
        }

        switch (so2grade){
            case "1":
                so2grade ="좋음";
                icon_so2grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_smile));
                break;
            case "2":
                so2grade ="보통";
                icon_so2grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_soso));
                break;
            case "3":
                so2grade ="나쁨";
                icon_so2grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_bad));
                break;
            case "4":
                so2grade ="매우 나쁨";
                icon_so2grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_cry));
                break;
        }

        switch (cograde){
            case "1":
                cograde ="좋음";
                icon_cograde.setImageDrawable(getResources().getDrawable(R.drawable.ic_smile));
                break;
            case "2":
                cograde ="보통";
                icon_cograde.setImageDrawable(getResources().getDrawable(R.drawable.ic_soso));
                break;
            case "3":
                cograde ="나쁨";
                icon_cograde.setImageDrawable(getResources().getDrawable(R.drawable.ic_bad));
                break;
            case "4":
                cograde ="매우 나쁨";
                icon_cograde.setImageDrawable(getResources().getDrawable(R.drawable.ic_cry));
                break;
        }

        switch (o3grade){
            case "1":
                o3grade ="좋음";
                icon_o3grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_smile));
                break;
            case "2":
                o3grade ="보통";
                icon_o3grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_soso));
                break;
            case "3":
                o3grade ="나쁨";
                icon_o3grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_bad));
                break;
            case "4":
                o3grade ="매우 나쁨";
                icon_o3grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_cry));
                break;
        }

        switch (no2grade){
            case "1":
                no2grade ="좋음";
                icon_no2grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_smile));
                break;
            case "2":
                no2grade ="보통";
                icon_no2grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_soso));
                break;
            case "3":
                no2grade ="나쁨";
                icon_no2grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_bad));
                break;
            case "4":
                no2grade ="매우 나쁨";
                icon_no2grade.setImageDrawable(getResources().getDrawable(R.drawable.ic_cry));
                break;
        }

//        tv_pm10grade.setText(pm10grade);
//        tv_pm25grade.setText(pm25grade);
//        tv_so2grade.setText(so2grade);
//        tv_cograde.setText(cograde);
//        tv_o3grade.setText(o3grade);
//        tv_no2grade.setText(no2grade);

        //xml에서 구글맵 객체가져오기
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);


//        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.maps)).getMapAsync(this);
//        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        UiSettings uiSettings = map.getUiSettings();
//        uiSettings.setZoomControlsEnabled(true);

//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        String locationProvider = LocationManager.NETWORK_PROVIDER;
//          location=lm.getLastKnownLocation(locationProvider);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng( new LatLng(latitude,longitude)));
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom( new LatLng(latitude,longitude),16);
        googleMap.animateCamera(zoom);

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(latitude,longitude));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        options.title("졸음발생지역");
        googleMap.addMarker(options);
        Log.i("위도",latitude+","+longitude);
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}