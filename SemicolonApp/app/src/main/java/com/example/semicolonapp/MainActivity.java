package com.example.semicolonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;


    ImageButton Home, Map, Music, Setting;
    TextView latitude, longitude;
    TextView curr_weather, humidity, wind, curr_temp, max_temp, min_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        //날씨 정보 가져오기 관련 코드
        curr_weather = (TextView) findViewById(R.id.curr_weather);
        humidity = (TextView) findViewById(R.id.humidity);
        wind = (TextView) findViewById(R.id.wind);
        curr_temp = (TextView) findViewById(R.id.curr_temp);
        max_temp = (TextView) findViewById(R.id.max_temp);
        min_temp = (TextView) findViewById(R.id.min_temp);


        //액티비티 이동 버튼 관련 코드
        Home = (ImageButton) findViewById(R.id.Home);
        Map = (ImageButton) findViewById(R.id.Map);
        Music = (ImageButton) findViewById(R.id.Music);
        Setting = (ImageButton) findViewById(R.id.Setting);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
//                    case R.id.Home:
//                        Intent intent1 = new Intent(timeline.this, signup.class);
//                        startActivity(intent1);
//                        break;
                    case R.id.Map:
                        Intent intent2 = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.Music:
//                        Intent intent4 = new Intent(timeline.this, profile.class);
//                        startActivity(intent4);
                        break;
                    case R.id.Setting:
//                        Intent intent5 = new Intent(timeline.this, profile.class);
//                        startActivity(intent4);
                        break;

                }
            }
        };

        Home.setOnClickListener(onClickListener);
        Map.setOnClickListener(onClickListener);
        Music.setOnClickListener(onClickListener);
        Setting.setOnClickListener(onClickListener);


        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //gps 권한 관리
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);


    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        double longitude1 = location.getLongitude();
        double latitude1 = location.getLatitude();
        longitude.setText("Longitude: " + longitude1 + "\n");
        latitude.setText("Latitude: " + latitude1);
        getWeatherDate(latitude1, longitude1);

        Log.i("location", "Latitude:" + location.getLatitude());
        Log.i("location", "Longitude:" + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }


    private void getWeatherDate(double lat, double lng) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lng + "&units=imperial&appid=cbc16f370cdaaf4fb2b2b211507a5c59&lang=kr";
        ReceiveWeatherTask receiveUseTask = new ReceiveWeatherTask();
        receiveUseTask.execute(url);
    }

    private class ReceiveWeatherTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            try {

                HttpURLConnection conn = (HttpURLConnection) new URL(strings[0]).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(reader);

                    String readed;
                    while ((readed = in.readLine()) != null) {
                        JSONObject jsonObject = new JSONObject(readed);
                        return jsonObject;
                    }

                } else {
                    return null;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //Log.i("info", jsonObject.toString());
            if (jsonObject != null) {

                String iconName = "";
                String nowTemp = "";
                String maxTemp = "";
                String minTemp = "";

                String humiditys = "";
                String speed = "";
                String main = "";
                String description = "";

                try {

                    iconName = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                    nowTemp = jsonObject.getJSONObject("main").getString("temp");
                    humiditys = jsonObject.getJSONObject("main").getString("humidity");
                    minTemp = jsonObject.getJSONObject("main").getString("temp_min");
                    maxTemp = jsonObject.getJSONObject("main").getString("temp_max");
                    speed = jsonObject.getJSONObject("wind").getString("speed");
                    main = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                    description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                 //description = transferWeather(description); //현재 날씨 영문을 한글로 바꿔줌
                final String msg = "날씨: " + description + "  습도: " + humiditys + "%   풍속: " + speed + "m/s " + "현재온도: " + nowTemp + "/최저: " + minTemp + "/최고: " + maxTemp;
                Log.i("msg", msg);

                String celsius_now =fahernheit_tocelsius(Double.parseDouble(nowTemp));
                String celsius_max =fahernheit_tocelsius(Double.parseDouble(maxTemp));
                String celsius_min =fahernheit_tocelsius(Double.parseDouble(minTemp));

                curr_weather.setText(msg);
                //curr_weather.setText(description);
//                humidity.setText("습도 "+humiditys+"%");
//                wind.setText("바람 "+speed+"m/s");
//                curr_temp.setText(celsius_now+"ºC");
//                max_temp.setText("최고 "+celsius_max+"ºC");
//                min_temp.setText("최저 "+celsius_min+"ºC");
            }
        }
    }

    //화씨에서 썹시로 변환 메소드
    private String fahernheit_tocelsius(Double faher_temp) {
        Double cels_temp= (faher_temp- 32.0)/1.8;
        cels_temp =  Math.ceil(cels_temp);
        return cels_temp.toString();
    }




}