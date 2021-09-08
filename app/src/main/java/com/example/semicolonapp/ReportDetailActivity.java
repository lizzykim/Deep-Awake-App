package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

    String date,address,weather,temperature,humidity;
    Double latitude,longitude;
    TextView tv_date,tv_location,tv_weather,tv_temperature,tv_humidity;
    GoogleMap map;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportdetailactivity);

        init();


    }

    void init(){

        //xml 주소 참조
        tv_date = (TextView)findViewById(R.id.date);
        tv_location = (TextView)findViewById(R.id.location);
        tv_weather= (TextView)findViewById(R.id.weather);
        tv_temperature= (TextView)findViewById(R.id.temperature);
        tv_humidity= (TextView)findViewById(R.id.humidity);



        //intent로 data전달 받기
        date = getIntent().getStringExtra("date");
        address = getIntent().getStringExtra("address");
        weather = getIntent().getStringExtra("weather");
        temperature = getIntent().getStringExtra("temperature");
        humidity = getIntent().getStringExtra("humidity");

        latitude =  Double.valueOf(getIntent().getStringExtra("lat"));
        longitude = Double.valueOf(getIntent().getStringExtra("long"));
        Log.i("위도",latitude+","+longitude);


        //뷰에 뿌려주기
        tv_date.setText("일자: "+date);
        tv_location.setText("위치: "+address);
        tv_weather.setText(weather);
        tv_temperature.setText(temperature +"ºC");
        tv_humidity.setText(humidity+"%");


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