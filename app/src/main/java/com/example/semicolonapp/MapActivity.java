package com.example.semicolonapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.KakaoCustomTabsClient;
import com.kakao.sdk.navi.NaviClient;
import com.kakao.sdk.navi.model.CoordType;
import com.kakao.sdk.navi.model.NaviOption;
import com.kakao.sdk.navi.model.RpOption;
import com.kakao.sdk.navi.model.VehicleType;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/*참고링크
(실시간 내 위치 표시)
*https://www.youtube.com/watch?v=AJiVBAP7rck&list=PLVsNizTWUw7GMJ_jrWYsvIUHRq_XfjPA2&index=52(코틀린)
* https://www.youtube.com/watch?v=ifoVBdtXsv0&t=1304s (자바)
* 위에 두개 짬뽕

(휴게소 누르면 infowindow 창 뜨는거)
https://wonpaper.tistory.com/250
* */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    boolean isPermissionGranted; //false로 초기화
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;//gps를 사용해서 좌표값을 가져오는 객체
    private LocationCallback locationCallback; //fusedLocationClient 의 응답값을 받아서 처리하는 콜백
    private Location currentlocation; //현재 위치 담는 변수

    //json 객체 담을 변수
    ArrayList<String> name = new ArrayList<>();//휴게소명
    ArrayList<String> road_type = new ArrayList<>();//도로종류
    ArrayList<String> road_route_name = new ArrayList<>();//도로노선
    ArrayList<Float> latitude = new ArrayList<>();//위도
    ArrayList<Float> longitude = new ArrayList<>();//경도
    ArrayList<String> gas_station = new ArrayList<>();//주유소 유무
    ArrayList<String> lpg_charge = new ArrayList<>();//LPG충전소유무
    ArrayList<String> electric_charge = new ArrayList<>();//전기차충전소유무
    ArrayList<String> phone = new ArrayList<>();//휴게소 전화번호

    private Button goto_rest_area; //휴게소 보여주는 버튼
    private Button restarea_info; //휴게소 정보 보여줌


    private ImageButton Home,Report, Map, Music, Setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);

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
                        Intent intent1 = new Intent(MapActivity.this, MainActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.Report:
                        Intent intent5 = new Intent(MapActivity.this, ReportActivity.class);
                        startActivity(intent5);
                        break;

                    case R.id.Map:
//                        Intent intent2 = new Intent(MainActivity.this, MapActivity.class);
//                        startActivity(intent2);
//                        break;
                    case R.id.Music:
                        Intent intent3 = new Intent(MapActivity.this, SongActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.Setting:
                        Intent intent4 = new Intent(MapActivity.this, SettingActivity.class);
                        startActivity(intent4);
                        break;

                }
            }
        };

        Home.setOnClickListener(onClickListener);
        Report.setOnClickListener(onClickListener);
        //Map.setOnClickListener(onClickListener);
        Music.setOnClickListener(onClickListener);
        Setting.setOnClickListener(onClickListener);

        // Kakao SDK 초기화
        KakaoSdk.init(this, "feefb8e4372ffe114b0f7aa28978b946");

        goto_rest_area = (Button) findViewById(R.id.goto_restarea_btn);//가까운 휴게소 위치 보여주기 btn

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //누르면 가까운 휴게소가 보이는 곳으로 줌 해줘
                Toast.makeText(MapActivity.this, "가까운 휴게소 지도에 띄어주기", Toast.LENGTH_SHORT).show();

                //1)현재 위치에서 가장 가까운 휴게소 계산하기 getNearestArea()
                //getNearstArea()


                //2) 휴게소 좌표값(위도,경도) showRestArea에 인자로 넘겨주기
                showRestArea(latitude.get(5), longitude.get(5), name.get(5), road_type.get(5),road_route_name.get(5),gas_station.get(5),lpg_charge.get(5),electric_charge.get(5),phone.get(5)); //listarray의 첫번째 부터 넘겨주기(임시)
            }
        };
        goto_rest_area.setOnClickListener(listener);


        //전국 휴게소들 정보 확인할 수 있는 버튼(임시)
        restarea_info = (Button) findViewById(R.id.restarea_info_btn);
        restarea_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //인텐트로 arraylist 8개 다 넘겨주기
                Intent intent = new Intent(MapActivity.this, ServiceAreaInfoActivity.class);
                intent.putStringArrayListExtra("name", name);
                intent.putStringArrayListExtra("road_type", road_type);
                intent.putStringArrayListExtra("road_route_name", road_route_name);
                //intent.putStringArrayListExtra("latitude",latitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putStringArrayListExtra("gas_station", gas_station);
                intent.putStringArrayListExtra("lpg_charge", lpg_charge);
                intent.putStringArrayListExtra("electric_charge", electric_charge);
                intent.putStringArrayListExtra("phone", phone);
                startActivity(intent);
            }
        });


        checkMyPermission(); //권한 확인

        if (isPermissionGranted) { //권환 허용 -> 지도 보여주기
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this); //안드로이드에게 맵을 사용할 수 있게 신호 보냄.그럼 안드가 onMapReady를 호출해줌
        }

        //Service.json 파일에서 json객체 가져오기
        try {
            JSONObject jsonObject = new JSONObject(JsonDataFromAsset()); //jsonObject는 ServicArea.json파일
            JSONArray jsonArray = jsonObject.getJSONArray("ServiceArea");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject service_area_data = jsonArray.getJSONObject(i);//service_area_data 에  JSONObject형태로 정보들 저장됨
                name.add(service_area_data.getString("휴게소명"));
                road_type.add(service_area_data.getString("도로종류"));
                road_route_name.add(service_area_data.getString("도로노선명"));
                latitude.add((float) service_area_data.getDouble("위도"));
                longitude.add((float) service_area_data.getDouble("경도"));
                gas_station.add(service_area_data.getString("주유소유무"));
                lpg_charge.add(service_area_data.getString("LPG충전소유무"));
                electric_charge.add(service_area_data.getString("전기차충전소유무"));
                phone.add(service_area_data.getString("휴게소전화번호"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("MapActivity", "name:"+ name);
//        Log.d("MapActivity", "road_type:"+ road_type);
//        Log.d("MapActivity", "road_route_name:"+ road_route_name);
//        Log.d("MapActivity", "latitude:"+ latitude);

    }

    //json 파일 접근하는 메소드
    private String JsonDataFromAsset() {

        String json = null;
        try {
            InputStream inputStream = getAssets().open("ServiceArea.json");
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    //권한 확인
    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {  //허용
                Toast.makeText(MapActivity.this, "권한 허용", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true; //false -> true 일 경우, 권한 허용
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) { //거절
                Toast.makeText(MapActivity.this, "권한 거절", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) { //허용 거절 둘다 한할떄 계속 경고 알림 띄우기
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * OnMapReadyCallback의 콜백 메서드
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupdateLocationListener(); //죄표값 갱신 해줌
    }

    //좌표값 갱신을 등록해주는 메소드
    @SuppressLint("MissingPermission")
    private void setupdateLocationListener() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setInterval(10000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) { //위치 좌표값이 없을 경우 메소드 정료
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    //Log.d("location", "$i ${location.latitude},${location.longitude}");
                    Log.d("location", "Latitude:" + location.getLatitude());
                    Log.d("location", "Longitude:" + location.getLongitude());
                    currentlocation = location;
                    Log.d("currentlocation",""+currentlocation.getLatitude());
                    Log.d("currentlocation",""+currentlocation.getLongitude());
                    setLastLocation(currentlocation);
                }
            }
        };
        //Location 요청 함수 호출 (locationRequest, locationCallback) 담아 보내
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void setLastLocation(Location location) {

        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(myLocation).title("현재 위치");
        CameraPosition cameraOptions = new CameraPosition.Builder().target(myLocation).zoom(20.0f).build();
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(cameraOptions);


        mMap.getUiSettings().setZoomControlsEnabled(true);//zoom 확대 축소
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);//상단에 오른쪽 gps 그림 뜨고 누르면 내 현재 위치로 줌인 됨
        mMap.clear();
        mMap.addMarker(marker);
        mMap.moveCamera(camera);
    }

    //가까운 휴게소 위치 보여주기
    //인수 넘길때 위도 ,경도로 넘겨주기
    private void showRestArea(Float latitude, Float longitude, String name, String road_type,String road_route_name, String gas_station, String lpg_charge, String electric_charge,String phone) {

        LatLng nearest_restarea = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(nearest_restarea).title(name + " 휴게소").snippet("도로종류: " + road_type+"\n도로노선명: "+road_route_name+"\n주유소유무: "+gas_station+"\nLPG충전소유무: "+lpg_charge+"\n전기차충전소유무: "+electric_charge+"\n전화번호: "+phone); //휴게소 이름으로 바꿔주기
        CameraPosition cameraOptions = new CameraPosition.Builder().target(nearest_restarea).zoom(15.0f).build();
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(cameraOptions);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(marker);
        mMap.animateCamera(camera);

        //마커누르면, 휴게소 정보 알려주는 infowindow 뜸
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(MapActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                //휴게소 명
                TextView name = new TextView(MapActivity.this);
                name.setTextColor(Color.BLACK);
                name.setGravity(Gravity.CENTER);
                name.setTypeface(null, Typeface.BOLD);
                name.setText(marker.getTitle());

                TextView road_type = new TextView(MapActivity.this);
                road_type.setTextColor(Color.GRAY);
                road_type.setGravity(Gravity.LEFT);
                road_type.setText(marker.getSnippet());

                info.addView(name);
                info.addView(road_type);

                return info;
            }
        });

        //info window를 누르면, 해당 목적지로 네비게이션 경로 시작
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                makeToast();
                //Uri uri=  NaviClient.getInstance().navigateWebUrl(new com.kakao.sdk.navi.model.Location("카카오 판교오피스","127.108640", "37.402111"), new NaviOption(CoordType.WGS84, VehicleType.FIRST, RpOption.SHORTEST,true,"127.01611744933815","37.65187803439617"));
                Uri uri=  NaviClient.getInstance().navigateWebUrl(new com.kakao.sdk.navi.model.Location(name,longitude.toString(), latitude.toString()), new NaviOption(CoordType.WGS84, VehicleType.FIRST, RpOption.SHORTEST,true,currentlocation.getLongitude()+"",currentlocation.getLatitude()+""));
                Log.e("name: ", name);
                Log.e("longitude: ", longitude.toString());
                Log.e("latitude: ", latitude.toString());
                opennavi(uri);
            }
        });


    }

    private void opennavi(Uri uri) {
        KakaoCustomTabsClient.INSTANCE.open(this,uri);
    }

    private void makeToast() {
        Toast.makeText(this, "navi로 이동합니다",Toast.LENGTH_SHORT).show();
    }



}

//-------------------------------------json 객체 정보 가져오기-----------------------------------------------

//arraylist 변수 선언(o)
//json 객체 가져와서 arraylist 에 대입(json파일에 접근 포함) (o)
//휴게소 정보 액티비티로 옮길때, intent로 arraylist 다 serviceinfo 액티비티로 넘겨주기(o)

//info에서 할일.
//전달 받은 arraylist 전송 받기 oncreate에서(0)
//어댑터 객체 만들기
//recycler 뷰에 어댑터랑 레이아웃 메니져 연결
//끝