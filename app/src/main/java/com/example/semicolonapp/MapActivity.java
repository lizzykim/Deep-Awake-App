package com.example.semicolonapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
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

import com.example.semicolonapp.data.DataHolder;
import com.example.semicolonapp.data.GetGuardianNumberResponse;
import com.example.semicolonapp.network.RetrofitClient;
import com.example.semicolonapp.network.ServiceApi;
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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*????????????
(????????? ??? ?????? ??????)
*https://www.youtube.com/watch?v=AJiVBAP7rck&list=PLVsNizTWUw7GMJ_jrWYsvIUHRq_XfjPA2&index=52(?????????)
* https://www.youtube.com/watch?v=ifoVBdtXsv0&t=1304s (??????)
* ?????? ?????? ??????

(????????? ????????? infowindow ??? ?????????)
https://wonpaper.tistory.com/250
* */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    boolean isPermissionGranted; //false??? ?????????
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;//gps??? ???????????? ???????????? ???????????? ??????
    private LocationCallback locationCallback; //fusedLocationClient ??? ???????????? ????????? ???????????? ??????
    private Location currentlocation; //?????? ?????? ?????? ??????

    //json ?????? ?????? ??????
    ArrayList<String> name = new ArrayList<>();//????????????
    ArrayList<String> road_type = new ArrayList<>();//????????????
    ArrayList<String> road_route_name = new ArrayList<>();//????????????
    ArrayList<Float> latitude = new ArrayList<>();//??????
    ArrayList<Float> longitude = new ArrayList<>();//??????
    ArrayList<String> gas_station = new ArrayList<>();//????????? ??????
    ArrayList<String> lpg_charge = new ArrayList<>();//LPG???????????????
    ArrayList<String> electric_charge = new ArrayList<>();//????????????????????????
    ArrayList<String> phone = new ArrayList<>();//????????? ????????????

    private Button goto_rest_area; //????????? ???????????? ??????
    //private Button restarea_info; //????????? ?????? ?????????


    private ImageButton Home,Report, Map, Music, Setting;

    public TextToSpeech textToSpeech; //TTS
    int speechStatus;
    //?????? ?????????
    Intent SttIntent;
    SpeechRecognizer mRecognizer;
    TextToSpeech tts;//?????? ?????????
    final int PERMISSION = 1;
    Context cThis ; // Context ??????
    private ServiceApi service;//retrofit ??????
    public String guardianNumber ="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);

        //???????????? ?????? ?????? ?????? ??????
        Home = (ImageButton)findViewById(R.id.Home);
        Report = (ImageButton)findViewById(R.id.Report);
        Map = (ImageButton)findViewById(R.id.Map);
        Music = (ImageButton)findViewById(R.id.Music);
        Setting = (ImageButton)findViewById(R.id.Setting);

        service = RetrofitClient.getClient().create(ServiceApi.class); //RETROFIT ??????


        //Log.i("data!!!", DataHolder.getPhonenumber());
        getguardiannum(DataHolder.getUseremail());


        // ??????????????? 6.0?????? ???????????? ???????????? ????????? ??????
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }


        //TTS ????????????
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int ttsLang = textToSpeech.setLanguage(Locale.KOREAN);
                    if(ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang ==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS","???????????? ???????????? ????????????.");
                    }else{
                        Log.i("TTS", "????????? ??????");
                    }
                    Log.i("TTS","TTS ????????? ??????");
                }else{
                    Toast.makeText(getApplicationContext(),"TTS ???????????? ?????????????????????!",Toast.LENGTH_SHORT).show();
                }
            }
        });



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
                        Intent intent3 = new Intent(MapActivity.this, AlarmActivity.class);
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

        // Kakao SDK ?????????
        KakaoSdk.init(this, "feefb8e4372ffe114b0f7aa28978b946");



//        goto_rest_area = (Button) findViewById(R.id.goto_restarea_btn);//????????? ????????? ?????? ???????????? btn
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //????????? ????????? ???????????? ????????? ????????? ??? ??????
//                Toast.makeText(MapActivity.this, "????????? ????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
//
//                //1)?????? ???????????? ?????? ????????? ????????? ???????????? getNearestArea()
//                //getNearstArea()
//
//
//                //2) ????????? ?????????(??????,??????) showRestArea??? ????????? ????????????
//                showRestArea(latitude.get(75), longitude.get(75), name.get(75), road_type.get(75),road_route_name.get(75),gas_station.get(75),lpg_charge.get(75),electric_charge.get(75),phone.get(75)); //listarray??? ????????? ?????? ????????????(??????)
//            }
//        };
//        goto_rest_area.setOnClickListener(listener);


        //?????? ???????????? ?????? ????????? ??? ?????? ??????(??????)
//        restarea_info = (Button) findViewById(R.id.restarea_info_btn);
//        restarea_info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) { //???????????? arraylist 8??? ??? ????????????
//                Intent intent = new Intent(MapActivity.this, ServiceAreaInfoActivity.class);
//                intent.putStringArrayListExtra("name", name);
//                intent.putStringArrayListExtra("road_type", road_type);
//                intent.putStringArrayListExtra("road_route_name", road_route_name);
//                //intent.putStringArrayListExtra("latitude",latitude);
//                intent.putExtra("latitude", latitude);
//                intent.putExtra("longitude", longitude);
//                intent.putStringArrayListExtra("gas_station", gas_station);
//                intent.putStringArrayListExtra("lpg_charge", lpg_charge);
//                intent.putStringArrayListExtra("electric_charge", electric_charge);
//                intent.putStringArrayListExtra("phone", phone);
//                startActivity(intent);
//            }
//        });
//

        checkMyPermission(); //?????? ??????

        if (isPermissionGranted) { //?????? ?????? -> ?????? ????????????
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this); //????????????????????? ?????? ????????? ??? ?????? ?????? ??????.?????? ????????? onMapReady??? ????????????
        }

        //Service.json ???????????? json?????? ????????????
        try {
            JSONObject jsonObject = new JSONObject(JsonDataFromAsset()); //jsonObject??? ServicArea.json??????
            JSONArray jsonArray = jsonObject.getJSONArray("ServiceArea");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject service_area_data = jsonArray.getJSONObject(i);//service_area_data ???  JSONObject????????? ????????? ?????????
                name.add(service_area_data.getString("????????????"));
                road_type.add(service_area_data.getString("????????????"));
                road_route_name.add(service_area_data.getString("???????????????"));
                latitude.add((float) service_area_data.getDouble("??????"));
                longitude.add((float) service_area_data.getDouble("??????"));
                gas_station.add(service_area_data.getString("???????????????"));
                lpg_charge.add(service_area_data.getString("LPG???????????????"));
                electric_charge.add(service_area_data.getString("????????????????????????"));
                phone.add(service_area_data.getString("?????????????????????"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//

        //3??? ?????? ?????? ??????????????????
        new Handler().postDelayed(mMyTask,3000);


    }

    private  Runnable mMyTask = new Runnable() {
        @Override
        public void run() {
            showRestArea(latitude.get(75), longitude.get(75), name.get(75), road_type.get(75),road_route_name.get(75),gas_station.get(75),lpg_charge.get(75),electric_charge.get(75),phone.get(75)); //listarray??? ????????? ?????? ????????????(??????)
        }
    };

    //json ?????? ???????????? ?????????
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

    //?????? ??????
    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {  //??????
                Toast.makeText(MapActivity.this, "?????? ??????", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true; //false -> true ??? ??????, ?????? ??????
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) { //??????
                Toast.makeText(MapActivity.this, "?????? ??????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) { //?????? ?????? ?????? ????????? ?????? ?????? ?????? ?????????
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * OnMapReadyCallback??? ?????? ?????????
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupdateLocationListener(); //????????? ?????? ??????
    }

    //????????? ????????? ??????????????? ?????????
    @SuppressLint("MissingPermission")
    private void setupdateLocationListener() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setInterval(10000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) { //?????? ???????????? ?????? ?????? ????????? ??????
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
        //Location ?????? ?????? ?????? (locationRequest, locationCallback) ?????? ??????
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void setLastLocation(Location location) {

        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(myLocation).title("?????? ??????");
        CameraPosition cameraOptions = new CameraPosition.Builder().target(myLocation).zoom(20.0f).build();
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(cameraOptions);


        mMap.getUiSettings().setZoomControlsEnabled(true);//zoom ?????? ??????
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
        mMap.setMyLocationEnabled(true);//????????? ????????? gps ?????? ?????? ????????? ??? ?????? ????????? ?????? ???
        mMap.clear();
        mMap.addMarker(marker);
        mMap.moveCamera(camera);
    }

    //????????? ????????? ?????? ????????????
    //?????? ????????? ?????? ,????????? ????????????
    private void showRestArea(Float latitude, Float longitude, String name, String road_type,String road_route_name, String gas_station, String lpg_charge, String electric_charge,String phone) {

        LatLng nearest_restarea = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(nearest_restarea).title(name + " ?????????").snippet("????????????: " + road_type+"\n???????????????: "+road_route_name+"\n???????????????: "+gas_station+"\nLPG???????????????: "+lpg_charge+"\n????????????????????????: "+electric_charge+"\n????????????: "+phone); //????????? ???????????? ????????????
        CameraPosition cameraOptions = new CameraPosition.Builder().target(nearest_restarea).zoom(15.0f).build();
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(cameraOptions);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(marker);
        mMap.animateCamera(camera);

        //???????????????, ????????? ?????? ???????????? infowindow ???
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(MapActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                //????????? ???
                TextView name1 = new TextView(MapActivity.this);
                name1.setTextColor(Color.BLACK);
                name1.setGravity(Gravity.CENTER);
                name1.setTypeface(null, Typeface.BOLD);
                name1.setText(marker.getTitle());

                TextView road_type = new TextView(MapActivity.this);
                road_type.setTextColor(Color.GRAY);
                road_type.setGravity(Gravity.LEFT);
                road_type.setText(marker.getSnippet());

                info.addView(name1);
                info.addView(road_type);

                return info;
            }
        });

        //info window??? ?????????, ?????? ???????????? ??????????????? ?????? ??????
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                speakCheckInBackground();
            }
        });
    }

    public void speakCheckInBackground() {
        textToSpeech.speak(name.get(75) + "???????????? ??? ????????? ???????????????? ???????????? '??????', ????????? '??????' ?????? ???????????????", TextToSpeech.QUEUE_FLUSH, null);
        new Waiter().execute();
    }

    class Waiter extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (textToSpeech.isSpeaking()){
                try{Thread.sleep(1000);}catch (Exception e){}
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //TTS has finished speaking. WRITE YOUR CODE HERE
            voicedriver();
        }
    }

    public void voicedriver(){
            try {
                SttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
                SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

                mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(SttIntent); //??????????????? ?????? ?????? ???????????? ????????? ?????? ????????? ??????
            } catch (SecurityException e) {
                e.printStackTrace();
            }
    }


    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // ????????? ????????? ??????????????? ??????
            Toast.makeText(getApplicationContext(), "???????????? ??????", Toast.LENGTH_SHORT).show();
            Log.d("tst5", "??????");
        }

        @Override
        public void onBeginningOfSpeech() {
            // ????????? ???????????? ??? ??????
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // ???????????? ????????? ????????? ?????????
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // ?????? ???????????? ????????? ??? ????????? buffer??? ??????
        }

        @Override
        public void onEndOfSpeech() {
            // ???????????? ???????????? ??????
        }

        @Override
        public void onError(int error) {
            // ???????????? ?????? ?????? ????????? ???????????? ??? ??????
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "??????????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "???????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "????????? ????????????";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "?????? ??? ??????";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER ??? ??????";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "????????? ?????????";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "????????? ????????????";
                    break;
                default:
                    message = "??? ??? ?????? ?????????";
                    break;
            }

            //Toast.makeText(getApplicationContext(), "?????? ?????? : " + message, Toast.LENGTH_SHORT).show();
            Log.d("tst5", "onError: " + message);
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            Log.i("STT", "?????????: " + rs[0]);
            FuncVoiceOrderCheck(rs[0]);
        }

        private void FuncVoiceOrderCheck(String VoiceMsg){
            if(VoiceMsg.length()<1) return;

            VoiceMsg = VoiceMsg.replace(" ", "");//????????????

            if(VoiceMsg.indexOf("??????") > -1 || VoiceMsg.indexOf("???") > -1){
                Toast.makeText(MapActivity.this, VoiceMsg,Toast.LENGTH_SHORT).show();
                Log.e("Voice",VoiceMsg);

                //????????? ????????? TTS
                speechStatus = textToSpeech.speak("???, ???????????????. ???????????? ???????????? ???????????????.",TextToSpeech.QUEUE_FLUSH,null);
                if(speechStatus == TextToSpeech.ERROR){
                    Log.e("TTS","???????????? ???????????? ???????????? ??? ?????? ??????");
                }

                if(DataHolder.getIsSendSMS().equals("true")){  //Asksmsactvitiy?????? ?????????, ??? ????????? ???????????? ??????????????? ???????????????
                    //??????????????? ????????????
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(guardianNumber, null, "??????????????? ????????? ???????????? "+name.get(75)+" ???????????? ??????????????????. \n -Deep Awake", null, null);
                        Toast.makeText(getApplicationContext(), "??????????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ??????!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }


                //????????? ????????????
                makeToast();
                startnavi();
            }

            if(VoiceMsg.indexOf("??????") > -1 || VoiceMsg.indexOf("??????") > -1){
                Log.e("Voice",VoiceMsg);
                Toast.makeText(MapActivity.this,VoiceMsg,Toast.LENGTH_SHORT).show();
                //????????? ????????? TTS
                speechStatus = textToSpeech.speak("???, ???????????????. ?????? ?????? ??????????????????. ",TextToSpeech.QUEUE_FLUSH,null);
                if(speechStatus == TextToSpeech.ERROR){
                    Log.e("TTS","???????????? ???????????? ???????????? ??? ?????? ??????");
                }
                finish();
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // ?????? ?????? ????????? ????????? ??? ?????? ??? ??????
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // ?????? ???????????? ???????????? ?????? ??????
        }
    };


    public void startnavi(){
        Uri uri=  NaviClient.getInstance().navigateWebUrl(new com.kakao.sdk.navi.model.Location(name.get(75),longitude.get(75).toString(), latitude.get(75).toString()), new NaviOption(CoordType.WGS84, VehicleType.FIRST, RpOption.SHORTEST,true,currentlocation.getLongitude()+"",currentlocation.getLatitude()+""));
        opennavi(uri);
    }


    private void opennavi(Uri uri) {
        KakaoCustomTabsClient.INSTANCE.open(this,uri);
    }

    private void makeToast() {
        Toast.makeText(this, "navi??? " +name.get(75) +" ???????????? ???????????????",Toast.LENGTH_SHORT).show();
    }



    //mapactivity????????? ??? ????????? ???????????? ????????? ?????? ????????????
    public void getguardiannum(String useremail){
        service.getGuardianNumber(useremail).enqueue(new Callback<GetGuardianNumberResponse>() {
            @Override
            public void onResponse(Call<GetGuardianNumberResponse> call, Response<GetGuardianNumberResponse> response) {
                GetGuardianNumberResponse result = response.body();
                Log.d("message", "" + result.getMessage());

                if(result.getCode() ==200){
                    guardianNumber = result.getGuardiannumber();
                    Log.d("????????? ??????", "" + result.getGuardiannumber());

                }
            }

            @Override
            public void onFailure(Call<GetGuardianNumberResponse> call, Throwable t) {
                Log.e("????????? ?????? ???????????? ??????", t.getMessage());
                t.printStackTrace();
            }
        });

    }





}

//-------------------------------------json ?????? ?????? ????????????-----------------------------------------------

//arraylist ?????? ??????(o)
//json ?????? ???????????? arraylist ??? ??????(json????????? ?????? ??????) (o)
//????????? ?????? ??????????????? ?????????, intent??? arraylist ??? serviceinfo ??????????????? ????????????(o)

//info?????? ??????.
//?????? ?????? arraylist ?????? ?????? oncreate??????(0)
//????????? ?????? ?????????
//recycler ?????? ???????????? ???????????? ????????? ??????
//???