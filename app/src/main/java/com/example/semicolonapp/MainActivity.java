package com.example.semicolonapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.semicolonapp.adapter.ReportAdapter;
import com.example.semicolonapp.brain.Analyzer;
import com.example.semicolonapp.brain.Constants;
import com.example.semicolonapp.brain.SerialCommand;
import com.example.semicolonapp.brain.SerialConnector;
import com.example.semicolonapp.brain.SignalHolder;
import com.example.semicolonapp.data.AirData;
import com.example.semicolonapp.data.Attentiondata;
import com.example.semicolonapp.data.DataHolder;
import com.example.semicolonapp.data.EEGdata;
import com.example.semicolonapp.data.GetNameResponse;
import com.example.semicolonapp.data.Meditationdata;
import com.example.semicolonapp.data.RAWdata;
import com.example.semicolonapp.data.ReportItemData;
import com.example.semicolonapp.data.ReportItemResponse;
import com.example.semicolonapp.data.weatherData;
import com.example.semicolonapp.network.GpsInfo;
import com.example.semicolonapp.network.RetrofitClient;
import com.example.semicolonapp.network.ServiceApi;
import com.google.android.gms.maps.model.LatLng;
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//날씨, 위치 정보는 죄표만 MainActivity에서 받고 <GPS 클래스> 만들어서 경도, 위치만 받는 클래스 따로 만들어서 객체 생성하는 걸로!
//public class MainActivity extends AppCompatActivity implements LocationListener {
    public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ////람다
    //private String mURL = "https://0nmhkvy9nf.execute-api.ap-northeast-2.amazonaws.com/semicolon";
    private String mURL = "https://w7rm40xsh3.execute-api.ap-northeast-2.amazonaws.com/semicolon2";
    private String connMethod ="POST";
    private String bodyJson;
    private static final int LOAD_SUCCESS=101;


    private ServiceApi service;//retrofit 관련
    private GpsInfo gps;//경위도 가져오기


    private TGDevice tgDevice;//thinkgear
    private BluetoothAdapter bluetoothAdapter; //블루투스 어댑터


    /////////////////뇌파관련
    public TextView mTextConn, mTextConnStrength,mTextRawDesc,mTextValueDesc,mTextRenderRaw; //연결 상태, 강도, 주파수 분석중입니다
    public RenderingView mRenderRaw,mRenderValue; //뇌파 그림
    private Analyzer mAnalyzer;
    private SignalHolder mSignalHolder;

    private int[] mEEGRawBuffer = null; //뇌파 512길이 버퍼
    private int[] mSignalBuffer = new int[3];
    private int mEEGRawBufferIndex = 0;

    //final boolean rawEnabled = false;
    private final boolean rawEnabled = true; //Shows raw data in textViewRawData

    private boolean mSendMindSignal = false;

    // Serial control
    private SerialListener mListener = null;
    private SerialConnector mSerialConn = null;

    public LinearLayout mModeMonitoring;



    /////////////////////뇌파 알고리즘 관련 변수
    int value;
    int timeLeft = 0; //시간이 얼마나 흘렀는지 알려줌
    int[] ha300 = new int[300]; //헤드셋 착용 직후 30 초 이후, 300 초 동안 측정되는 high alpha 수치가 들어가는 배열
    int[] la300 = new int[300]; //헤드셋 착용 직후 30 초 이후, 300 초 동안 측정되는 low alpha 수치가 들어가는 배열
    int[] hb300 = new int[300]; //헤드셋 착용 직후 30 초 이후, 300 초 동안 측정되는 high beta 수치가 들어가는 배열
    int[] lb300 = new int[300]; //헤드셋 착용 직후 30 초 이후, 300 초 동안 측정되는 low beta 수치가 들어가는 배열

    int halahblbHeadIndex300 = 0;//ha300, la300, hb300,lb300의 인덱스. 현재 들어가야 할 곳
    Boolean wait30 = false;//false일 경우 졸음체크해도됨, true일 경우 알람이 울린 후 30초가 지나지 않았으니 졸음체크 하면 안됨
    int wait30Now = 0;//wait30이 true일 때 몇초 지나갔는지 (30초가 지났을 경우 졸음여부측정)

    int standard_ha = 0;//330초 때 기준점
    int standard_la = 0;
    int standard_hb = 0;
    int standard_lb = 0;
    int tempBindoHa = 0;//빈도 구할 때 temp
    int tempBindoLa = 0;
    int tempBindoHb = 0;
    int tempBindoLb = 0;

    //음악 알람을 눈 깜박임으로 끄기 위해, 5 칸짜리 배열 blinkTime 을 두고 0 으로 초기화할 것이다.
    //만약 [0] 과 [1], [1] 과 [2], [2] 와 [3], [3] 과 [4] 의 차이가,
    //currentTimeMillis() 로 <= 1000, 세 번 모두 AND 조건으로 묶이면 { 알람 꺼짐. }
    long[] blinkTime = new long[5];
    int blinkTimeIndex = 0;

    private int mSerialLogCount = 0;
    private static final int SERIAL_LOG_COUNT_MAX = 300;



    //----- Controller layout
    private int mFreqDrawingMode = Constants.FREQ_DRAW_MODE_ALPHA_TO_GAMMA;
    private int mCurrentViewMode = Constants.VIEW_MODE_MONITORING;
    private TextView mTextSerial;


    /////////////////////////버튼,글씨 등등
    ImageButton Home,Report, Map, Music, Setting;
    TextView latitude, longitude,name;
    TextView curr_weather, humidity, wind, curr_temp, max_temp, min_temp;
    static String useremail="";
    double lati = 0;
    double longi = 0;
    Button sleepcheck_btn; //졸음이라고 인지될때 == 버튼 (임시)
    double current_lati = 0;
    double current_longi = 0;
    String current_lati_string = "";
    String current_longi_string = "";
    String current_address ="";
    String current_time ="";
    String current_weather ="";
    String current_temp="";
    String current_humidity="";

    //대기질
    String sidoname="";
//    String current_pm10Value="";
//    String current_pm25Value="";
//    String current_so2Value="";
//    String current_coValue="";
//    String current_o3Value="";
//    String current_no2Value="";
//
//    String current_pm10Grade="";
//    String current_pm25Grade="";
//    String current_so2Grade="";
//    String current_coGrade="";
//    String current_o3Grade="";
//    String current_no2Grade="";

    //AsyncTask 변수들
    String pm10Value="";
    String pm25Value="";
    String so2Value="";
    String coValue="";
    String o3Value="";
    String no2Value="";


    String pm10Grade="";
    String pm25Grade="";
    String so2Grade="";
    String coGrade="";
    String o3Grade="";
    String no2Grade="";




    //adapter에 추가된 데이터를 저장하기 위한 ReportItem형의 arraylist
    ArrayList<ReportItemData> reportItems = new ArrayList<ReportItemData>();
    ReportAdapter adapter;
   // ReportAdapter adapter = new ReportAdapter(this,reportItems);




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

        name =(TextView)findViewById(R.id.username);//사용자 이름




        //액티비티 이동 버튼 관련 코드
        Home = (ImageButton) findViewById(R.id.Home);
        Report = (ImageButton) findViewById(R.id.Report);
        Map = (ImageButton) findViewById(R.id.Map);
        Music = (ImageButton) findViewById(R.id.Music);
        Setting = (ImageButton) findViewById(R.id.Setting);


        mTextSerial = (TextView)findViewById(R.id.text_logs);
        sleepcheck_btn = (Button)findViewById(R.id.sleep_check); //졸음 체크 참조

        service = RetrofitClient.getClient().create(ServiceApi.class); //RETROFIT 객체

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
                    case R.id.Report:
                        Intent intent5 = new Intent(MainActivity.this, ReportActivity.class);
                        startActivity(intent5);
                        break;
                    case R.id.Music:
                        Intent intent3 = new Intent(MainActivity.this, SongActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.Setting:
                        Intent intent4 = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.text_title_render1:
                        mFreqDrawingMode++;
                        if (mFreqDrawingMode > Constants.FREQ_DRAW_MODE_MAX)
                            mFreqDrawingMode = Constants.FREQ_DRAW_MODE_ALPHA_TO_GAMMA;
                        break;

                    case R.id.sleep_check: //졸음이 측정될때 (임시!!!!!)
                        Toast.makeText(MainActivity.this ,"현재 졸음이 감지되었습니다!",Toast.LENGTH_SHORT).show();//날자로 toast띄우기
                        /* 실행되야할 알고리즘
                        눌렀을때의 1)날짜 2)경위도 3)온도 4) 날씨 보내기 (어디로????)
                        1) DataHolder에 date, latlong 등 변수 만들어서 저장  -> 2) nodejs로 sql 서버로 전달 maybe..


                        >>ReportFragment에서  reportItems 에 add 로 전달한 데이터 추가해준 뒤(해당 과정은 reportadapter에서 실행되야될듯???),
                        >> ReportFragment 에서 adapter.get날짜, adpater.get경위도 로 전달 받은 데이터에 접근
                         */

                        //@@@@@step1. 현재 날짜, 경위도, 온도 , 날씨 캡쳐하기
                        /*
                        a) 현재 경위도 불러오기
                        b) 현재 주소 불러오기
                        c) 현재 날짜 불러오기
                        d) 현재 경위도로  온도, 날씨 캡쳐하기
                        */

                        //a)현재 경위도 불러오기
                        current_lati = getLogLat().latitude;
                        current_longi = getLogLat().longitude;

                        current_lati_string = String.valueOf(current_lati);
                        current_longi_string = String.valueOf(current_longi);

                        Log.i ("Capture" , "캡쳐 경도:" + current_lati_string);
                        Log.i ("Capture" , "캡쳐 위도:" + current_longi_string);

                        //b)현재 주소 불러오기
                        current_address = getCurrentAddress(current_lati,current_longi);
                        Log.i ("Capture" , "캡쳐 주소:" + current_address);

                        //c)현재 날짜 불러오기
                        Long now = System.currentTimeMillis();
                        Date mDate = new Date(now);
                        SimpleDateFormat simpledate = new SimpleDateFormat("MM월 dd일 HH:mm:ss");
                        current_time = simpledate.format(mDate);

                        Log.i("Capture","캡쳐 시간:" + current_time);

                        //d)현재 경위도로  온도, 날씨 불러오기(에러 : 버튼 처음 눌렀을때는 날씨, 습도, 온도가 불러와지지 않음) ->oncreate일때, weatherThread를 실행하면 해결완료
                        //oncreate일때, weatherThread를 실행-> onclick했을때의 현재 날씨, 습도, 온도가 current_weather,current_humidity,current_temp가 저장됨
                        Log.i("Capture","캡쳐 날씨: " + current_weather);
                        Log.i("Capture","캡쳐 습도: " +  current_humidity);
                        Log.i("Capture","캡쳐 온도: " + current_temp);

                        Toast.makeText(MainActivity.this, "졸음 시점 환경 데이터:  경도=" + current_lati_string +" 위도=" + current_longi_string + " 위치="+current_address +" 시간="+current_time+" 날씨="+current_weather +" 온도="+current_temp+" 습도="+current_humidity,Toast.LENGTH_SHORT).show();




                        //e)현재(캡쳐시) 대기정보 불러오기
//                        Log.i("Capture","캡쳐 미세먼지: " + current_pm10Value);
//                        Log.i("Capture","캡쳐 초미세먼지: " +  current_pm25Value);
//                        Log.i("Capture","캡쳐 아황산가스: " + current_so2Value);
//                        Log.i("Capture","캡쳐 일산화탄소: " + current_coValue);
//                        Log.i("Capture","캡쳐 오존: " +  current_o3Value);
//                        Log.i("Capture","캡쳐 이산화질소: " + current_no2Value);
//
//                        Log.i("Capture","캡쳐 미세먼지등급: " + current_pm10Grade);
//                        Log.i("Capture","캡쳐 초미세먼지등급: " +  current_pm25Grade);
//                        Log.i("Capture","캡쳐 아황산가스등급: " + current_so2Grade);
//                        Log.i("Capture","캡쳐 일산화탄소등급: " + current_coGrade);
//                        Log.i("Capture","캡쳐 오존등급: " +  current_o3Grade);
//                        Log.i("Capture","캡쳐 이산화질소등급: " + current_no2Grade);



                        Log.i("Capture","캡쳐 미세먼지: " + pm10Value);
                        Log.i("Capture","캡쳐 초미세먼지: " +  pm25Value);
                        Log.i("Capture","캡쳐 아황산가스: " + so2Value);
                        Log.i("Capture","캡쳐 일산화탄소: " + coValue);
                        Log.i("Capture","캡쳐 오존: " +  o3Value);
                        Log.i("Capture","캡쳐 이산화질소: " + no2Value);

                        Log.i("Capture","캡쳐 미세먼지등급: " + pm10Grade);
                        Log.i("Capture","캡쳐 초미세먼지등급: " +  pm25Grade);
                        Log.i("Capture","캡쳐 아황산가스등급: " +  so2Grade);
                        Log.i("Capture","캡쳐 일산화탄소등급: " + coGrade);
                        Log.i("Capture","캡쳐 오존등급: " + o3Grade);
                        Log.i("Capture","캡쳐 이산화질소등급: " + no2Grade);


                        Log.i("reportitems",reportItems.toString());


                        //@@@@@step2. 현재 정보들 mysql driverrecord 테이블에 저장
                        //putdriverrecord(new ReportItemData(current_lati_string,current_longi_string,current_time,current_address,current_weather,current_temp,current_humidity));
                        putdriverrecord(new ReportItemData(current_lati_string,current_longi_string,current_time,current_address,current_weather,current_temp,current_humidity,pm10Value,pm25Value,so2Value,coValue,o3Value,no2Value,pm10Grade,pm25Grade,so2Grade,coGrade,o3Grade,no2Grade));

                        break;

                }
            }
        };

        //Home.setOnClickListener(onClickListener);
        Report.setOnClickListener(onClickListener);
        Map.setOnClickListener(onClickListener);
        Music.setOnClickListener(onClickListener);
        Setting.setOnClickListener(onClickListener);

        sleepcheck_btn.setOnClickListener(onClickListener); //임시


        /////@@@주의! mainactivity 에서 시작한다면  밑에 end까지는 주석 처리하고 돌려볼것!
        //메인 화면 들어올때 username SQL,RETROFIT2 으로 받아오는 코드//
//        if(DataHolder.getUseremail() == null){
//            Intent intent = getIntent(); //로그인 유저의 이메일 가져오기(로그인에서!)
//            useremail = intent.getExtras().getString("useremail");
//            DataHolder.setUseremail(useremail);
//            Log.i("useremail", useremail);
//            showUserName(useremail); //사용자 이름을 알아내기 위해 로그인한 이메일을 대입.
//        }
//        showUserName(DataHolder.getUseremail()); //사용자 이름을 알아내기 위해 로그인한 이메일을 대입.
        //end

        //현재 날씨 캡쳐하는 스레드
        weatherThread thread = new weatherThread();
        thread.start();

        //현재 대기질 캡쳐하는 스레드 sidoname이 존재할때 쓰레드 시작.
//        if(sidoname != null) {
//            AirConditionThread thread2 = new AirConditionThread();
//            thread2.start();
//        }


        //실시간 경위도 파악하는 코드 & 사용자 허용받기//
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);

        getLogLat(); //경도 위도 얻는 메소드

        getAirCondition(sidoname); //도(지역) 으로  실시간 대기질 정보 얻는 메서드



        ////뇌파 관련
        mTextConn = (TextView)findViewById(R.id.text_conn);//연결 상태
        mTextConnStrength = (TextView)findViewById(R.id.text_conn_strength);//연결 강도
        mRenderRaw = (RenderingView)findViewById(R.id.layout_raw);//주파수 나오는 그래프
        mRenderValue = (RenderingView)findViewById(R.id.layout_value);

        mModeMonitoring = (LinearLayout)findViewById(R.id.layout_monitoring);


        mTextRenderRaw = (TextView)findViewById(R.id.text_title_render1);//뇌파 주파수를 분석중입니다.
        mTextRenderRaw.setOnClickListener(onClickListener);


        mCurrentViewMode = Constants.VIEW_MODE_MONITORING;


        //1)주파수 이름 보이게 하는 코드
        mTextRawDesc = (TextView)findViewById(R.id.text_freq_desc); //뇌파 주파수 이름들
        SpannableString sText1 = new SpannableString("Delta, Theta, Alpha L, Alpha H, Beta L, Beta H, Gamma M, Gamma H");
        sText1.setSpan(new ForegroundColorSpan(0xFF00FF00), 0, 5, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFF00AA00), 7, 12, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFF0000FF), 14, 21, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFF0000AA), 23, 30, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFFFF0000), 32, 38, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFFAA0000), 40, 46, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFFAAAAAA), 48, 55, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFF777777), 57, 64, 0);
        mTextRawDesc.append(sText1);

        mTextValueDesc = (TextView)findViewById(R.id.text_value_desc);
        SpannableString sText = new SpannableString("Attention, Meditation, Blink, Heart Rate, Poor Signal");
        sText.setSpan(new ForegroundColorSpan(Color.RED), 0, 9, 0);
        sText.setSpan(new ForegroundColorSpan(Color.BLUE), 11, 21, 0);
        sText.setSpan(new ForegroundColorSpan(Color.GREEN), 23, 28, 0);
        sText.setSpan(new ForegroundColorSpan(0xFF444444), 30, 40, 0);
        sText.setSpan(new ForegroundColorSpan(0xFFAAAAAA), 42, 53, 0);
        mTextValueDesc.append(sText);



        //----- Initialize processing core
        initialize(); //뇌파 핵심 코드

    }

    //졸음 현재 캡쳐 데이터 nodejs.express로 mysql에 저장하는 메소드
    private void putdriverrecord(ReportItemData data) {
        service.postReportRecord(data).enqueue(new Callback<ReportItemResponse>() {
            @Override
            public void onResponse(Call<ReportItemResponse> call, Response<ReportItemResponse> response) {
                ReportItemResponse result = response.body();
                Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ReportItemResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "운전 레코드를 삽입하지 못했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("운전레코드 삽입 에러 발생", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌
            }
        });
    }

    //졸음 시점 순간 (캡쳐시점) 날씨  <<<<<<<<<캡처!!!!>>>>>>>시의 날씨
        private weatherData getCurrentWeather(double current_lati, double current_longi) {

        String nowTemp = "";
        String humiditys = "";
        String description = "";
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + current_lati + "&lon=" + current_longi + "&units=imperial&appid=cbc16f370cdaaf4fb2b2b211507a5c59&lang=kr";
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
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
                    if (jsonObject != null) {

                        try {
                            nowTemp = jsonObject.getJSONObject("main").getString("temp");
                            humiditys = jsonObject.getJSONObject("main").getString("humidity");
                            description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                            Log.i("온도 썹시", nowTemp);
                            Log.i("온도 화시", fahernheit_tocelsius(Double.valueOf(nowTemp)));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        final String msg = "캡쳐날씨22: " + description + "  습도: " + humiditys + "%  " + "현재온도: " + nowTemp ;
//                        Log.i("Capture", msg);
//                        Log.i("Capture",  humiditys);
//                        Log.i("Capture",  nowTemp);
//                        return  description;  //String 3개 있는 자료형 만들것!
                        //return  new weatherData(nowTemp,humiditys,description);  //String 3개 있는 자료형 만들것!
                        return  new weatherData(fahernheit_tocelsius(Double.valueOf(nowTemp)),humiditys,description);//nowTemp가  현재 화씨이기때문에 썹시로 변환해줄 것
                    }
                }
            } else {
                Log.i("TAG","날씨 사이트와 연결 불가능");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }

    //Thread.
    //졸음 시점 순간 (캡쳐시점) 대기질  <<<<<<<<<캡처!!!!>>>>>>>
//    private AirData getCurrentAirCondition(String sidoname) throws JSONException {
//
//        String pm10Value = "";
//        String pm25Value = "";
//        String so2Value = "";
//        String coValue = "";
//        String o3Value = "";
//        String no2Value = "";
//
//
//        String pm10Grade = "";
//        String pm25Grade = "";
//        String so2Grade = "";
//        String coGrade = "";
//        String o3Grade = "";
//        String no2Grade = "";
//        String url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=RDhwJsKNojKg%2BfDAA%2F86XHsR11xWnim58B9cIh%2FO9I4Ro4eSPLNLL8zrqT%2BWkWPtYv%2FomBDW7CIZuHpLGbDv1g%3D%3D&returnType=json&numOfRows=1&pageNo=1&sidoName=" + sidoname + "&ver=1.0";
//
//
//
//
//        try {
//            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Content-type", "application/json");
//            Log.i("Response code", conn.getResponseCode() + "");
//
//            BufferedReader rd;
//            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else {
//                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//            }
//
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//                Log.i("sbbbbb",sb+"");
//
//                JSONObject jsonObject = new JSONObject(line);
//                Log.i("sbbbbb",jsonObject.toString());
//
//                //if (jsonObject != null   ) {
//                if (jsonObject.getJSONObject("response").getJSONObject("header").getString("resultCode").equals("00")) {
//                    try {
//                        pm10Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("pm10Value");
//                        pm25Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("pm25Value");
//                        so2Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("so2Value");
//                        coValue = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("coValue");
//                        o3Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("o3Value");
//                        no2Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("no2Value");
//
//
//                        pm10Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("pm10Grade");
//                        pm25Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("pm25Grade");
//                        so2Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("so2Grade");
//                        coGrade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("coGrade");
//                        o3Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("o3Grade");
//                        no2Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("no2Grade");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    return new AirData(pm10Value,pm25Value,so2Value,coValue,o3Value,no2Value,pm10Grade,pm25Grade,so2Grade,coGrade,o3Grade,no2Grade);
//
//                }
//            }
//        } catch (ProtocolException protocolException) {
//            protocolException.printStackTrace();
//        } catch (MalformedURLException malformedURLException) {
//            malformedURLException.printStackTrace();
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//
//        }catch (Exception e){
//            e.printStackTrace();
//
//        }
//        return null;
//    }



    private LatLng getLogLat() {
    //private void getLogLat() {
        gps = new GpsInfo(this);

        if(gps.isGetLocation()){
            lati = gps.getLatitude();
            longi = gps.getLongitude();
            longitude.setText("Longitude: " + lati + "\n");
            latitude.setText("Latitude: " + longi);
            Log.i("location", "Latitude:" + lati);
            Log.i("location", "Longitude:" + longi);

            //위도, 경도로 <날씨> 가져오는 메서드
            getWeatherDate(lati, longi);

            //위도 , 경도로 <주소> 가져오는 메서드
            getCurrentAddress(lati, longi);


        }else{
            gps.showSettingAlert();
        }

        return new LatLng(lati,longi);  //경위도 반환
    }

    //initialize(): 액티비티 생성될 때 이 메서드를 호출함. 이 안에서 setupBT(), doStuff()가 호출되고, 디바이스와 BT연결이 정상적으로 되면 mHandler를 통해 데이터를 받을 수 있음.
    private void initialize() {
        mAnalyzer = new Analyzer(); //brain 에서 import
        mSignalHolder = new SignalHolder();//brain 에서 import

        mEEGRawBuffer = new int[Constants.EEG_RAW_DATA_LEN]; //512int크기의 자료형
        Arrays.fill(mEEGRawBuffer, 0); //512크기 버퍼를 0으로 채움
        Arrays.fill(mSignalBuffer, 0); //3크기 버퍼를 0으로 채움

        setupBT();//블루투스 어댑터 생성 & TG 객체 생성
        doStuff();        // connect TGDevice. If succeeded, Handler will receive CONNECTED message and call tgDevice.start()
    }

    private void doStuff() {
        if (tgDevice.getState() != TGDevice.STATE_CONNECTING
                && tgDevice.getState() != TGDevice.STATE_CONNECTED)
            tgDevice.connect(rawEnabled);
    }


    private void setupBT() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //블루투스 지원 유뮤 확인
        //if(bluetoothAdapter ==null){
        if(bluetoothAdapter ==null & !bluetoothAdapter.isEnabled()){
            Toast.makeText(this, "블루투스 지원 안함", Toast.LENGTH_SHORT).show();
            finish(); //왜 쓰는 거지?
            return; //return 안하면 else문으로 넘어감
        }else{
            Toast.makeText(this, "블루투스 지원", Toast.LENGTH_SHORT).show();
            /* create the TGDevice */
            tgDevice = new TGDevice(bluetoothAdapter, mHandler);
        }
    }

    private void initializeSerial() {
        if (mListener == null)
            mListener = new SerialListener();

        //Initialize Serial connector and starts Serial monitoring thread.
        if (mSerialConn == null) {
            mSerialConn = new SerialConnector(this, mListener, mHandler);
            mSerialConn.initialize();
        }
    }

    ////주파수 그리는 부분이 null일때, xml주소 참조하고, rendering view 메소드로로
    private void initializeGraphics(){
        mRenderRaw = (RenderingView)findViewById(R.id.layout_raw);
        mRenderValue = (RenderingView)findViewById(R.id.layout_value);
        Log.d(TAG, "나 1111111");


        mRenderRaw.initializeGraphics(); //RenderingView의 메소드임
        mRenderValue.initializeGraphics();

        Log.d(TAG, "나 222222222222222222");

    }


    //mHandler: 헤드셋에서 전달되는 커맨드를 처리. 모든 뇌파 데이터 처리가 여기서 시작.
    private final Handler mHandler = new Handler(){

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage( Message msg) {
            switch (msg.what){
                case TGDevice.MSG_STATE_CHANGE:
                    Log.d(TAG, "state: MSG_STATE_CHANGE");

                    switch (msg.arg1){
                        case TGDevice.STATE_IDLE:        // Initial state of the TGDevice. Not connected to a headset
                            mTextConn.setText(" _STATE_IDLE");
                            break;
                        case TGDevice.STATE_CONNECTING:    // Attempting a connection to the headset
                            mTextConn.setText(" 연결 중...");
                            break;
                        case TGDevice.STATE_CONNECTED:    // A valid device hand been found and data is being received
                            Log.d(TAG, "state: STATE_CONNECTED");
                            mTextConn.setText(" 연결 완료");
                            makeToast( "연결이 완료되었습니다.");

                            //찾았다 !!!initializeGraphics를 안 거쳐서 그럼,,,근데 이 조건문은 없어도 되는 것까?
//                            if (mRenderRaw == null || mRenderValue == null)
//                                initializeGraphics();
                            initializeGraphics();
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:    // Could not connect to headset
                            mTextConn.setText(" 검색 실패");
                            break;
                        case TGDevice.STATE_NOT_PAIRED:    // A valid headset could not be found
                            mTextConn.setText(" _STATE_NOT_PAIRED");

                            break;
                        case TGDevice.STATE_DISCONNECTED:    // connection to the device is lost
                            mTextConn.setText(" 연결 실패");
                            break;
                    }
                    break;

                // Raw data
                case TGDevice.MSG_RAW_COUNT:
                    /*
                    Log.d(TAG, "state:MSG_RAW_COUNT");
                     if(mEEGRawBufferIndex == Constants.EEG_RAW_DATA_LEN) {
                     Log.d(TAG, "# Buffer full!!  Buffer size = "+(mEEGRawBufferIndex));
                     // Data full. Save array.
                     double[] copied = mSignalHolder.setEEGRawData(mEEGRawBuffer);
                     double[] ac = new double[copied.length];
                     Arrays.fill(ac, 0f);
                     // Analyze data
                     //mAnalyzer.realForwardFFT(copied);
                     // frequency = ((double)sampleRate / (double)FRAME_ELEMENT_COUNT) * freqIndex;

                     mAnalyzer.fftAutoCorrelation(copied, ac, true);		// raw data, auto correlation result, normalize or not

                     // Draw graph
                     mRenderRaw.drawRawGraph(ac);
                     mRenderRaw.invalidate();
                     } else {
                     // Some of raw data is lost.
                     Log.d(TAG, "# We got invalid raw data count!!  Array size = "+(mEEGRawBufferIndex));
                     }

                     Arrays.fill(mEEGRawBuffer, 0);
                     mEEGRawBufferIndex = 0;
                     */
                    break;

                // TGDevice.MSG_RAW_DATA is Deprecated....
                case TGDevice.MSG_RAW_DATA:
                    if (rawEnabled) {
                        //updateRawData(message.arg1);
                        //Log.w(TAG, "MSG_RAW_DATA: " + msg.arg1);

                        ///////RAWdata 객체 만들어서 서버에 넘겨주기////
//                        RAWdata raWdata = new RAWdata();
//                        raWdata.setRaw(String.valueOf(msg.arg1));
//                        postRawData(raWdata);
                        ///////////


                    }

                    break;
                case TGDevice.MSG_RAW_MULTI:
                        TGRawMulti tgRawMulti = (TGRawMulti) msg.obj;
                        Log.i(TAG, "Raw channel 1: " + tgRawMulti.ch1);
                        Log.i(TAG, "Raw channel 2: " + tgRawMulti.ch2);
                        Log.i(TAG, "Raw channel 3: " + tgRawMulti.ch3);
                        Log.i(TAG, "Raw channel 4: " + tgRawMulti.ch4);
                        Log.i(TAG, "Raw channel 5: " + tgRawMulti.ch5);
                        Log.i(TAG, "Raw channel 6: " + tgRawMulti.ch6);
                        Log.i(TAG, "Raw channel 7: " + tgRawMulti.ch7);
                        Log.i(TAG, "Raw channel 8: " + tgRawMulti.ch8);

                    break;
                case TGDevice.MSG_EEG_POWER:
                    //Log.d(TAG, "state:MSG_EEG_POWER");
                    TGEegPower ep =(TGEegPower)msg.obj;
                    TGEegPower ep_normalized = new TGEegPower();
                    if(ep!=null) {
                        mSignalHolder.setEEGBandData(ep);        // Remember

                        // Update view when it's monitoring mode
                        if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {

                            // normalize data
                            double[] arrayTemp = new double[8];
                            arrayTemp[0] = ep.delta;
                            arrayTemp[1] = ep.theta;
                            arrayTemp[2] = ep.lowAlpha;
                            arrayTemp[3] = ep.highAlpha;
                            arrayTemp[4] = ep.lowBeta;
                            arrayTemp[5] = ep.highBeta;
                            arrayTemp[6] = ep.lowGamma;
                            arrayTemp[7] = ep.midGamma;

                            //mAnalyzer.normalization(arrayTemp);
                            ep_normalized.delta = (int) arrayTemp[0];
                            ep_normalized.theta = (int) arrayTemp[1];
                            ep_normalized.lowAlpha = (int) arrayTemp[2];
                            ep_normalized.highAlpha = (int) arrayTemp[3];
                            ep_normalized.lowBeta = (int) arrayTemp[4];
                            ep_normalized.highBeta = (int) arrayTemp[5];
                            ep_normalized.lowGamma = (int) arrayTemp[6];
                            ep_normalized.midGamma = (int) arrayTemp[7];

//                            Log.d("ep", ep.delta+"" );
//                            Log.d("ep", ep.theta+"" );
//                            Log.d("ep", ep.lowAlpha+"" );
//                            Log.d("ep", ep.highAlpha+"" );
//                            Log.d("ep", ep.lowBeta+"" );
//                            Log.d("ep", ep.highBeta+"" );
//                            Log.d("ep", ep.lowGamma+"" );
//                            Log.d("ep", ep.midGamma+"" );
//
//                            Log.d("ep_normalized", ep_normalized.delta+"" );
//                            Log.d("ep_normalized", ep_normalized.theta+"" );
//                            Log.d("ep_normalized", ep_normalized.lowAlpha+"" );
//                            Log.d("ep_normalized", ep_normalized.highAlpha+"" );
//                            Log.d("ep_normalized", ep_normalized.lowBeta+"" );
//                            Log.d("ep_normalized", ep_normalized.highBeta+"" );
//                            Log.d("ep_normalized", ep_normalized.lowGamma+"" );
//                            Log.d("ep_normalized", ep_normalized.midGamma+"" );

                            if (mFreqDrawingMode == Constants.FREQ_DRAW_MODE_ALPHA_TO_GAMMA)
                                mRenderRaw.drawRelativePowerGraph(ep_normalized);
                            else if (mFreqDrawingMode == Constants.FREQ_DRAW_MODE_ALL_BAND)
                                mRenderRaw.drawFreqBandGraph(ep_normalized);

                            mRenderRaw.invalidate();
                        }

                        ///알고리즘시작
                        if ((ep.highAlpha != 0) && (ep.lowAlpha != 0) && (ep.highBeta != 0) && (ep.lowBeta != 0)) {  //하이알파, 로우알파, 하이베타, 로우베타 수치가 0 이 아닐때
                            timeLeft++;
                        }
                        if (timeLeft == 1) {
                            Log.v(TAG, "수정된거임");

                        }
                        if (timeLeft >= 30 && timeLeft < 330) {//30초 이상 330초 이하일 때
                            ha300[halahblbHeadIndex300] = ep_normalized.highAlpha;
                            la300[halahblbHeadIndex300] = ep_normalized.lowAlpha;
                            hb300[halahblbHeadIndex300] = ep_normalized.highBeta;
                            lb300[halahblbHeadIndex300] = ep_normalized.lowBeta;
                            halahblbHeadIndex300++;
                            //Log.v(TAG, "30초이상, 330초 미만이라 데이터를 쌓기만 하는중..");
                        } else if (timeLeft == 330) {//330초 때기준점을 잡는다(16000000이하의 수치가 몇회인지 기준점)
                            int toStdTempBindoHa = 0;
                            int toStdTempBindoLa = 0;
                            int toStdTempBindoHb = 0;
                            int toStdTempBindoLb = 0;

                            for (int i = 0; i < 300; i++) {
                                if (ha300[i] > 16000000) {
                                    toStdTempBindoHa++;
                                }
                                if (la300[i] > 16000000) {
                                    toStdTempBindoLa++;
                                }
                                if (hb300[i] > 16000000) {
                                    toStdTempBindoHb++;
                                }
                                if (lb300[i] > 16000000) {
                                    toStdTempBindoLb++;
                                }
                            }//for
                            standard_ha = toStdTempBindoHa;
                            //Log.v(TAG, "standard_ha:" + standard_ha);
                            standard_la = toStdTempBindoLa;
                            //Log.v(TAG, "standard_la:" + standard_la);
                            standard_hb = toStdTempBindoHb;
                            //Log.v(TAG, "standard_hb:" + standard_hb);
                            standard_lb = toStdTempBindoLb;
                            //Log.v(TAG, "standard_lb:" + standard_lb);
                            //Log.v(TAG, "330초가 되어 standard값을 구하였습니다!");
                        } else if (timeLeft > 330) {  //330초 이후 졸음여부 측정
                            //30초이후에는 항상 이곳에 들어옴
                            if (wait30 == true) {//인덱스30이전으로수정, 데이터 쌓기만 하고 졸음검사는 안함
                                wait30Now++;//처음여기들어오면 1초부터 시작
                                //Log.v(TAG, "wait30==true들어옴, wait30Now:" + wait30Now);
                                if (wait30Now == 1 && halahblbHeadIndex300 - 30 < 0) {//졸음이왔을때(wait30Now==1) 인덱스-30이음수이면 인덱스를30이전으로 하되 음수가 안되게 수정
                                    //halahblbHeadIndex300=halahblbHeadIndex300+299-29;
                                    halahblbHeadIndex300 = halahblbHeadIndex300 + 270;
                                    //Log.v(TAG, "알람이울렸을 때의 인덱스-30이 음수여서 인덱스값조정. 인덱스:" + halahblbHeadIndex300);
                                } else if (wait30Now == 1 && halahblbHeadIndex300 - 30 >= 0) {//졸음이 왔을 때(wait30Now==1)인덱스를 30이전으로 해주기
                                    halahblbHeadIndex300 -= 30;
                                    //Log.v(TAG, "알람이울렸을 때의 인덱스-30이 양수임. 인덱스값조정. 인덱스:" + halahblbHeadIndex300);
                                }
                                if (wait30Now == 31) {
                                    wait30 = false;
                                    //Log.v(TAG, "알람울리고30초 기다리는거 끝남");
                                }
                            }//if(wait30==true)
                            if (halahblbHeadIndex300 == 300) {//다시 배열의 처음부터 채움
                                halahblbHeadIndex300 = 0;
                                //Log.v(TAG, "300칸이 다 찼기에 다시 0번째 칸부터 시작!");
                            }
                            ha300[halahblbHeadIndex300] = ep_normalized.highAlpha;
                            la300[halahblbHeadIndex300] = ep_normalized.lowAlpha;
                            hb300[halahblbHeadIndex300] = ep_normalized.highBeta;
                            lb300[halahblbHeadIndex300] = ep_normalized.lowBeta;
                            //Log.v(TAG, "[현재 넣은 halahblbHEadIndex300]:" + halahblbHeadIndex300);
                            halahblbHeadIndex300++;
                            //------------------------
                            /*if(timeLeft==380){
                                wait30=true;
                            }*/

                            //------------------------
                            if (wait30 == false) {//졸음여부측정가능

                                tempBindoHa = 0;
                                tempBindoLa = 0;
                                tempBindoHb = 0;
                                tempBindoLb = 0;

                                for (int i = 0; i < 300; i++) {
                                    if (ha300[i] > 16000000) {
                                        tempBindoHa++;
                                    }
                                    if (la300[i] > 16000000) {
                                        tempBindoLa++;
                                    }
                                    if (hb300[i] > 16000000) {
                                        tempBindoHb++;
                                    }
                                    if (lb300[i] > 16000000) {
                                        tempBindoLb++;
                                    }
                                }//for
                                //Log.v(TAG, "졸음여부 측정하고 있습니다(wait30==false) standatd_ha*0.5738:" + standard_ha * 0.5738 + "tempbindoha:" + tempBindoHa);
                                //Log.v(TAG, "std_la*0.55:" + standard_la * 0.55 + ",tempBindoLa" + tempBindoLa + "|std_hb*0.2959:" + standard_hb * 0.2959 + ",tempBindoHb: " + tempBindoHb);
                                //Log.v(TAG, "|std_lb*0.3905:" + standard_lb * 0.3905 + "tempBindoLb:" + tempBindoLb);
                                //Log.v(TAG,"player playing?"+String.valueOf(player.isPlaying()));

//                                if ((player.isPlaying() == false) && ((standard_ha * 0.5738 > tempBindoHa) || (standard_la * 0.55 > tempBindoLa) || (standard_hb * 0.2959 > tempBindoHb) || (standard_lb * 0.3905 > tempBindoLb))) {
//                                    //알람이 울리지 않고 있고, 그 외 4 가지 조건이 OR 조건으로 더 일치하면 조건문 안으로 들어와, 알람을 울린다.
//                                    if (isPhoneAlarm.equals("true")) {
//                                        onBtnAlarmJ1();
//
//                                    }
//                                    if (isSmartWatch.equals("true")) {
//                                        onBtnAlarm2();
//                                    }
//                                    if (isOtherPerson.equals("true")) {
//                                        onBtnAlarm3();
//                                    }
//
//                                    String tempstr = "!!!!!알람울림.현재시간의 Minute:" + new Date().getMinutes();//getMinites:현재시간에서 분만 가져옴
//                                    Log.v(TAG, tempstr);
//
//                                    wait30 = true;
//                                    wait30Now = 0;//30초 기다리기 위해 현재는 0초임.
//
//                                }//알람울리는if문
                            }//if(wait30==false)
                        }//else if(timeLeft>330)
                    }
                        if (mSendMindSignal) {
                            int command = mSignalHolder.makeMoveCommand();
                            if (command > Constants.SERIAL_SUB_CMD_MOVE_NONE) {
                                sendMoveCommand(command);
                            }
                        }

                        ///EEGdata 객체 만들어서 서버에 넘겨주기////
                        postEEGData(new EEGdata(ep.delta,ep.theta,ep.lowAlpha,ep.highAlpha,ep.lowBeta,ep.highBeta,ep.lowGamma,ep.midGamma));
                        //////////////////

                        //EEG 람다로 보내기
                        sendEEGLamda(mURL,connMethod,new EEGdata(ep.delta,ep.theta,ep.lowAlpha,ep.highAlpha,ep.lowBeta,ep.highBeta,ep.lowGamma,ep.midGamma));

                        String tag="RAW Brain Waves";
                        Log.d(tag, "MSG_EEG_POWER: " + ep.delta + ", " + ep.theta  + ", " + ep.lowAlpha  + ", " + ep.highAlpha  + ", " + ep.lowBeta  + ", " + ep.highBeta + ", " + ep.lowGamma + ", " + ep.midGamma);
                        break;

                // Additional data (Poor signal, Heart rate)
                case TGDevice.MSG_POOR_SIGNAL:
                    //Log.d(TAG, "state:MSG_POOR_SIGNAL");

//                    mSignalHolder.setPoorSignal(msg.arg1);
//
//                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
//                        mRenderValue.drawValueGraph(0, 0, 0, msg.arg1, 0);
//                        mRenderValue.invalidate();
//                    }
//                    Log.d(TAG, "PoorSignal: " + msg.arg1 + "\n");
//                    break;


                    //연결 세기를 알수 있음
                    if(msg.arg1 >0){

                        float val = msg.arg1;
                        val -= 25;
                        val /= 175;
                        val = 1.0f - val;
                        val *= 100.0f;
                        mTextConnStrength.setText(String.valueOf((int)val));
                        Log.w(TAG, "Poor signal: " + msg.arg1);
                        Log.i(TAG, "connection strength: " +  val);
                    }

                    break;
                case TGDevice.MSG_HEART_RATE:
                    //Log.d(TAG, "state:MSG_HEART_RATE");
                    mSignalHolder.setHeartRate(msg.arg1);

                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(0, 0, 0, 0, msg.arg1);
                        mRenderValue.invalidate();
                    }
                    Log.d(TAG, "Heart rate: " + msg.arg1 + "\n");
                    break;
                // Pre-processed data
                case TGDevice.MSG_ATTENTION:
                    //Log.d(TAG, "state:MSG_ATTENTION");
                    mSignalHolder.setAttention(msg.arg1);

                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(msg.arg1, 0, 0, 0, 0);
                        mRenderValue.invalidate();
                    }
//	            	if(mSendMindSignal) {
//	            		sendMindSignal(msg.arg1, 0, 0);
//	            	}

                    postAttentionData(new Attentiondata(msg.arg1));
                    Log.d(TAG, "Attention: " + msg.arg1 + "\n");
                    break;

                case TGDevice.MSG_MEDITATION:
                    //Log.d(TAG, "state:MSG_MEDITATION");
                    mSignalHolder.setMeditation(msg.arg1);

                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(0, msg.arg1, 0, 0, 0);
                        mRenderValue.invalidate();
                    }
////	            	if(mSendMindSignal) {
////	            		sendMindSignal(0, msg.arg1, 0);
////	            	}

                    //람다로 보내는 코드
                    sendMeditationLamda(mURL,connMethod, new Meditationdata(msg.arg1));

                    //postMeditationData(new Meditationdata(msg.arg1));  //nodejs로 보내는 코드
                    Log.d(TAG, "Meditation: " + msg.arg1 + "\n");
                    break;

                case TGDevice.MSG_BLINK:
                    //Log.d(TAG, "state:MSG_BLINK");
                    mSignalHolder.setBlink(msg.arg1);
                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(0, 0, msg.arg1, 0, 0);
                        mRenderValue.invalidate();

                        //순순
                        if (blinkTimeIndex == 5) {
                            for (int i = 0; i < 5; i++)
                                blinkTime[i] = 0;
                            blinkTimeIndex = 0;
                        }
                        blinkTime[blinkTimeIndex] = System.currentTimeMillis();
                        blinkTimeIndex++;
                        if ((blinkTime[1] - blinkTime[0] > 0) && (blinkTime[1] - blinkTime[0] < 1000) && (blinkTime[2] - blinkTime[1] > 0) && (blinkTime[2] - blinkTime[1] < 1000) && (blinkTime[3] - blinkTime[2] > 0) && (blinkTime[3] - blinkTime[2] < 1000) && (blinkTime[4] - blinkTime[3] > 0) && (blinkTime[4] - blinkTime[3] < 1000)) {

                            //워치 알람 끄기
                            value = -10;

//                            // 소리 끄기
//                            if (player.isPlaying()) {
//                                player.stop();
//                            }
                        }
                        //순순
                    }
////	            	if(mSendMindSignal) {
////	            		sendMindSignal(0, 0, msg.arg1);
////	            	}
                    Log.d(TAG, "Blink: " + msg.arg1 + "\n");
                    break;

                // System notice
                case TGDevice.MSG_LOW_BATTERY:
                    Log.d(TAG, "state:MSG_LOW_BATTERY");
                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        //mTextSystem.setText("LOW_BATTERY");
                    }
                    // Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
                    break;
                // Arduino serial message
                case Constants.MSG_DEVICD_INFO:
                    Log.d(TAG, "state:MSG_DEVICD_INFO");
//                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
//                        mTextSerial.append((String) msg.obj);
//                    }
                    break;
                case Constants.MSG_DEVICE_COUNT:
                    Log.d(TAG, "state:MSG_DEVICE_COUNT");
//                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
//                        mTextSerial.append(Integer.toString(msg.arg1) + " device(s) found \n");
//                    }
                    break;
                case Constants.MSG_READ_DATA_COUNT:
                    Log.d(TAG, "state:MSG_READ_DATA_COUNT");
//                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
//                        mTextSerial.append(Integer.toString(msg.arg1) + " buffer received \n");
//                    }
                    break;
                case Constants.MSG_READ_DATA:
                    Log.d(TAG, "state:MSG_READ_DATA");
//                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
//                        SerialCommand cmd = null;
//                        if (msg.obj != null) {
//                            if (mSerialLogCount > SERIAL_LOG_COUNT_MAX) {
//                                mTextSerial.setText("");
//                                mSerialLogCount = 0;
//                            }
//
//                            cmd = (SerialCommand) msg.obj;
//                            mTextSerial.append(cmd.toString());
//                            mSerialLogCount++;
//                        }
//                    }
                    break;
                case Constants.MSG_SERIAL_ERROR:
                    Log.d(TAG, "state:MSG_SERIAL_ERROR");
//                    mTextSerial.append((String) msg.obj);
                    break;
                default:
                    break;

            } // End of switch()
        }// End of handleMessage()
    };



    //핸들러는 메인스레드와 workerthread의 메시지를 교환하는 도와주는 도우미역할을 한다 .
    private static class MyHandler extends Handler{
        private final WeakReference<MainActivity> weakReference;

        private MyHandler(MainActivity mainActivity) {
            weakReference = new WeakReference<MainActivity>(mainActivity);
        }


//        MessageQueue에 저장된 메시지들은 Looper에 의해 꺼내지면 handleMessage로 들어간다.
//        그럼 handleMessage()는 실제 UI 작업을 수행단다. 이것은 mainthread이다.
        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity mainActivity = weakReference.get();
            if(mainActivity!= null){
                switch (msg.what){
                    case LOAD_SUCCESS:
                        String jsonString = (String)msg.obj;
                        if(jsonString.isEmpty()){
                            Log.i("lambda","jsonString은!!!null입니다.");
                        }else{
                            Log.i("lambda","jsonString은!  "+jsonString);
                        }
                        break;
                }
            }
        }
    }

    private final MyHandler mmHandler = new MyHandler(this);



    public void sendEEGLamda(String mURL, String connMethod, EEGdata eegdata){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result;

                try {
                    URL url = new URL(mURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod(connMethod);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    JSONObject body = new JSONObject();

                    body.put("DELTA",eegdata.getEeg_delta());
                    body.put("THETA",eegdata.getEeg_theta());
                    body.put("LOWALPHA",eegdata.getEeg_lowAlpha());
                    body.put("HIGHALPHA",eegdata.getEeg_highAlpha());
                    body.put("LOWBETA",eegdata.getEeg_lowBeta());
                    body.put("HIGHBETA",eegdata.getEeg_highBeta());
                    body.put("LOWGAMMA",eegdata.getEeg_lowGamma());
                    body.put("MIDGAMMA",eegdata.getEeg_midGamma());


                    Log.i("lambda","body의 값은:  "+body.toString());
                    bodyJson=body.toString();
                    Log.i("lambda","bodyJSON의 값은:  "+bodyJson);

                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.write(bodyJson.getBytes("EUC-KR"));
                    wr.flush();
                    wr.close();

                    httpURLConnection.connect();

                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.i("lambda", "responseStatusCode는" +responseStatusCode );

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "EUC-KR");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        //Log.i("lambda","sb의 값은:  "+sb.toString().trim());
                    }

                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    result = sb.toString().trim();
                    //Log.i("lambda", "result의 값은:  "+result);


                } catch (Exception e) {
                    result = e.toString();
                }

                //Handler 사용함( 왜 handler를 사용하는 걸까?)
                //worker thread는 마지막에 헨들러를  이영하여 메인스레드로 메시지를 전달한다
                Message message = mmHandler.obtainMessage(LOAD_SUCCESS, result); //메세지 객체 생성(보낼 메시지를 안에 답는다)
                mmHandler.sendMessage(message);
                /*sendMessage를 통해 메시지를 전달하면, 해당 메시지는 mainthread의 messageQueue에 저장된다.
                messageQueue에 저장되면 Looper이 차례대로 메세지를 꺼내고 handleMessage()로 전달된다
                */

            }
        });
        thread.start();


    }



    public void sendMeditationLamda(String mURL, String connMethod, Meditationdata meditation) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result;

                try {
                    URL url = new URL(mURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod(connMethod);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    JSONObject body = new JSONObject();
                    body.put("MEDITATION", meditation.getMeditation());
                    Log.i("lambda","body의 값은:  "+body.toString());
                    bodyJson=body.toString();
                    Log.i("lambda","bodyJSON의 값은:  "+bodyJson);

                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.write(bodyJson.getBytes("EUC-KR"));
                    wr.flush();
                    wr.close();

                    httpURLConnection.connect();

                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.i("lambda", "responseStatusCode는" +responseStatusCode );

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "EUC-KR");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        Log.i("lambda","sb의 값은:  "+sb.toString().trim());
                    }

                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    result = sb.toString().trim();
                    Log.i("lambda", "result의 값은:  "+result);


                } catch (Exception e) {
                    result = e.toString();
                }

                //Handler 사용함( 왜 handler를 사용하는 걸까?)
                //worker thread는 마지막에 헨들러를  이영하여 메인스레드로 메시지를 전달한다
                Message message = mmHandler.obtainMessage(LOAD_SUCCESS, result); //메세지 객체 생성(보낼 메시지를 안에 답는다)
                mmHandler.sendMessage(message);
                /*sendMessage를 통해 메시지를 전달하면, 해당 메시지는 mainthread의 messageQueue에 저장된다.
                messageQueue에 저장되면 Looper이 차례대로 메세지를 꺼내고 handleMessage()로 전달된다
                */

            }
        });
        thread.start();


  }



    private void postMeditationData(Meditationdata meditation) {
        service.post_MEDITATION_train(meditation).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                //Toast.makeText(MainActivity.this, "운전자 Meditation 데이터 삽입 성공", Toast.LENGTH_SHORT).show();
                //Log.e(TAG, "운전자 Meditation 데이터 삽입 성공");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //Toast.makeText(MainActivity.this, "운전자 Meditation 데이터를 삽입하지 못했습니다.", Toast.LENGTH_SHORT).show();
                //Log.e("운전자 MEd 데이터 삽입 에러 발생", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌
            }
        });
    }

    private void postAttentionData(Attentiondata attention) {
        service.post_ATTENTION_train(attention).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                //Toast.makeText(MainActivity.this, "운전자 Attention 데이터 삽입 성공", Toast.LENGTH_SHORT).show();
                //Log.e(TAG, "운전자 Attention 데이터 삽입 성공");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //Toast.makeText(MainActivity.this, "운전자 Attention 데이터를 삽입하지 못했습니다.", Toast.LENGTH_SHORT).show();
                //Log.e("운전자 어텐션 데이터 삽입 에러 발생", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌
            }
        });
    }


    /////운전자 뇌파 학습용 데이터 서버에 전달(EEG)
    private void postEEGData(EEGdata data) {
        service.post_EEG_train(data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this, "운전자 EEG 데이터 삽입 성공", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "운전자 EEG 데이터 삽입 성공");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "운전자 EEG 데이터를 삽입하지 못했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("운전자 EEG 데이터 삽입 에러 발생", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌
            }
        });
    }


    /////운전자 뇌파 학습용 데이터 서버에 전달(RAW)
    private void postRawData(RAWdata data){
        service.post_RAW_train(data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this, "운전자 RAW 데이터 삽입 성공", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "운전자 RAW 데이터 삽입 성공");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "운전자 RAW 데이터를 삽입하지 못했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("운전자 RAW 데이터 삽입 에러 발생", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌
            }
        });
    }




    private void sendMoveCommand(int command) {
        SerialCommand cmd = new SerialCommand();
        cmd.setCommand(Constants.SERIAL_CMD_MOVE, 1, command);        // Msg ID, index, command
        //mSerialConn.sendCommand(cmd);
    }

    ///////////////왜 필요한지 모르겠는 애덜../////////////

    private void changeViewMode(int mode) {
        if (mode == Constants.VIEW_MODE_CONTROLLER) {
            if (mCurrentViewMode != mode) {
                mModeMonitoring.setVisibility(View.GONE);
                //mModeController.setVisibility(View.VISIBLE);
                mCurrentViewMode = Constants.VIEW_MODE_CONTROLLER;


                initializeSerial();
            }
        } else if (mode == Constants.VIEW_MODE_MONITORING) {
            if (mCurrentViewMode != mode) {
                mModeMonitoring.setVisibility(View.VISIBLE);
                //mModeController.setVisibility(View.GONE);

                mCurrentViewMode = Constants.VIEW_MODE_MONITORING;
            }
        }
    }

    public class SerialListener {
        public void onReceive(int msg, int arg0, int arg1, String arg2, Object arg3) {
            switch (msg) {
                case Constants.MSG_DEVICD_INFO:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(arg2);
                    }
                    break;
                case Constants.MSG_DEVICE_COUNT:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(Integer.toString(arg0) + " device(s) found \n");
                    }
                    break;
                case Constants.MSG_READ_DATA_COUNT:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(Integer.toString(arg0) + " buffer received \n");
                    }
                    break;
                case Constants.MSG_READ_DATA:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        SerialCommand cmd = null;
                        if (arg3 != null) {
                            if (mSerialLogCount > SERIAL_LOG_COUNT_MAX) {
                                mTextSerial.setText("");
                                mSerialLogCount = 0;
                            }

                            cmd = (SerialCommand) arg3;
                            // mTextSerial.append(cmd.toString());

                            mSerialLogCount++;
                        }
                    }
                    break;
                case Constants.MSG_SERIAL_ERROR:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(arg2);
                    }
                    break;
                case Constants.MSG_FATAL_ERROR_FINISH_APP:
                    finish();
                    break;
            }
        }
    }



    private void sendMindSignal(int attention, int meditation, int blink) {
        Arrays.fill(mSignalBuffer, 0);

        // Do not send value 0. Aduino cannot receive 0x00.
        // 0 < value < 128
        if (attention > 0) {
            mSignalBuffer[0] = attention;
        } else {
            mSignalBuffer[0] = mSignalHolder.getLatestAttention();
        }

        if (meditation > 0) {
            mSignalBuffer[1] = meditation;
        } else {
            mSignalBuffer[1] = mSignalHolder.getLatestMeditation();
        }

        if (blink > 0) {
            mSignalBuffer[2] = blink;
        } else {
            mSignalBuffer[2] = mSignalHolder.getLatestBlink();
        }

        SerialCommand cmd = new SerialCommand();
        cmd.setCommand(Constants.SERIAL_CMD_MIND_SIGNAL, mSignalBuffer);
        //mSerialConn.sendCommand(cmd);
    }

    private void setBackground(View v, boolean isEnabled) {
        if (isEnabled)
            v.setBackgroundColor(0xff0077aa);
        else
            v.setBackgroundColor(0xff999999);
    }

////////////////////////////////////////////////////////


    //위도 , 경도로 <<<<<주소>>>>>>> 가져오는 메서드
    private String getCurrentAddress(double latitude1, double longitude1) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude1,longitude1,1);
        }catch (IOException ioException){
            Toast.makeText(this,"Geocoder 서비스 사용불가",Toast.LENGTH_LONG).show();
        }catch (IllegalArgumentException illegalArgumentException){
            Toast.makeText(this,"잘못된 GPS좌표",Toast.LENGTH_LONG).show();
        }
        if(addresses != null){
            if(addresses.size()==0){
                Log.i("address", "입출력 오류 - 서버에서 주소변환시 에러발생" );
            }else{
                curr_temp.setText( addresses.get(0).getAdminArea()+" "+ addresses.get(0).getSubLocality() +" "+ addresses.get(0).getThoroughfare());
                Log.i("address", "주소 :" + addresses.get(0).getAdminArea()+" "+ addresses.get(0).getSubLocality() +" "+ addresses.get(0).getThoroughfare());

                if(addresses.get(0).getAdminArea().equals("서울특별시")){
                    sidoname = "서울";
                    Log.i("address", "시도는 "+ sidoname);
                }else if(addresses.get(0).getAdminArea().equals("경기도")){
                    sidoname = "경기";
                    Log.i("address", "시도는 "+ sidoname);
                }

                return addresses.get(0).getAdminArea()+" "+ addresses.get(0).getSubLocality() +" "+ addresses.get(0).getThoroughfare();
            }
        }
        return"";
    }

    //시도(경기도, 서울만 함) 로 실시간 <<<<<대기정보>>>>>> 가져오는 메서드
    private void getAirCondition(String sidoname){
        Log.i("sido", "url직전의 받아온 sidoname은 " +sidoname+ " 입니다.");
        String url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=RDhwJsKNojKg%2BfDAA%2F86XHsR11xWnim58B9cIh%2FO9I4Ro4eSPLNLL8zrqT%2BWkWPtYv%2FomBDW7CIZuHpLGbDv1g%3D%3D&returnType=json&numOfRows=1&pageNo=1&sidoName="+sidoname+"&ver=1.0";
        ReceiveAirConditionTask receiveAirConditionTask = new ReceiveAirConditionTask();
        receiveAirConditionTask.execute(url);

        }


    //시도(경기도, 서울만 함) 로 실시간 <<<<<대기정보>>>>>> 가져오는 메서드가 비동기로 실시간 돌아가게 하는 AsyncTank 상속받은 메서드
    /*
     * AsyncTask 에 대한 설명 링크 읽어볼것!
     * https://lktprogrammer.tistory.com/165
     */
    private class ReceiveAirConditionTask extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try{

                HttpURLConnection conn = (HttpURLConnection) new URL(strings[0]).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type","application/json");
                Log.i("Response code", conn.getResponseCode()+"");

                BufferedReader rd;
                if(conn.getResponseCode()>=200 && conn.getResponseCode()<=300){
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                }else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    //sb.append(line);
                    JSONObject jsonObject = new JSONObject(line);
                    return jsonObject;
                }
                rd.close();
                conn.disconnect();
                Log.i("sido AIR DATA" , sb.toString());

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i("sido AIR DATA", jsonObject.toString());
            ///////////여기서 JSON object해제해주기!///////////////
            if(jsonObject != null){
                //변수 선언( 수치 & 등급)
//                String pm10Value="";
//                String pm25Value="";
//                String so2Value="";
//                String coValue="";
//                String o3Value="";
//                String no2Value="";
//
//
//                String pm10Grade="";
//                String pm25Grade="";
//                String so2Grade="";
//                String coGrade="";
//                String o3Grade="";
//                String no2Grade="";

                try{

                    pm10Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("pm10Value");
                    pm25Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("pm25Value");
                    so2Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("so2Value");
                    coValue = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("coValue");
                    o3Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("o3Value");
                    no2Value = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("no2Value");


                    pm10Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("pm10Grade");
                    pm25Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("pm25Grade");
                    so2Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("so2Grade");
                    coGrade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("coGrade");
                    o3Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("o3Grade");
                    no2Grade = jsonObject.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0).getString("no2Grade");

                }catch (Exception e){
                    e.printStackTrace();
                }

                final String air = " pm10Value(미세먼지):"+ pm10Value +" pm25Value(초미세먼지):"+ pm25Value +" so2Value(아황산가스):"+ so2Value +" coValue(일산화탄소):"+ coValue +" o3Value(오존):"+ o3Value +" no2Value(이산화질소):"+ no2Value ;
                final String air_grade = " pm10Grade(미세먼지등급):"+ pm10Grade+" pm25Grade(초미세먼지등급):"+ pm25Grade +" so2Grade(아황산가스등급):"+ so2Grade +" coGrade(일산화탄소등급):"+ coGrade +" o3Grade(오존등급):"+ o3Grade +" no2Grade(이산화질소등급):"+ no2Grade ;     ;



                Log.i("air", air);
                Log.i("air_grade", air_grade);

            }


        }

    }


    //위도, 경도로 <<<<<날씨>>>>>> 가져오는 메서드
    private void getWeatherDate(double lat, double lng) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lng + "&units=imperial&appid=cbc16f370cdaaf4fb2b2b211507a5c59&lang=kr";
        ReceiveWeatherTask receiveUseTask = new ReceiveWeatherTask();
        receiveUseTask.execute(url);
    }


    //위도, 경도로 <<<<<<<날씨>>>>> 가져오는 메서드가 비동기로 실시간 돌아가게 하는 AsyncTask
    /*
    * AsyncTask 에 대한 설명 링크 읽어볼것!
    * https://lktprogrammer.tistory.com/165
    */
    private class ReceiveWeatherTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        //메인스레드 아니고 별게 작업스레드드
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
            //Log.i("weather", jsonObject.toString());
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
                //final String msg = "날씨: " + description + "  습도: " + humiditys + "%   풍속: " + speed + "m/s " + "현재온도: " + nowTemp + "/최저: " + minTemp + "/최고: " + maxTemp;
                final String msg = "날씨: " + description + "  습도: " + humiditys + "%   풍속: " + speed + "m/s " + "현재온도: " + fahernheit_tocelsius(Double.parseDouble(nowTemp)) + "/최저: " + fahernheit_tocelsius(Double.parseDouble(minTemp)) + "/최고: " + fahernheit_tocelsius(Double.parseDouble(maxTemp));
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

    //현재 날씨 가져오는 thread(졸음 캡쳐시 그 당시의 날씨) <<<<<캡쳐할때 실행되는 THREAD!!>>>>>
    class weatherThread extends Thread{
        public weatherThread() {
        }
        public void run(){
            current_temp = getCurrentWeather(current_lati,current_longi).getCurrentTemp();
            current_humidity= getCurrentWeather(current_lati,current_longi).getCurrentHumidity();
            current_weather = getCurrentWeather(current_lati,current_longi).getCurrentWeather();
        }
    }




//    class AirConditionThread extends Thread{
//        public AirConditionThread() {
//        }
//
//        @Override
//        public void run() {
//
//            if(sidoname.equals("경기")||sidoname.equals("서울")) {
//
//                try {
//                    current_pm10Value = getCurrentAirCondition(sidoname).getPm10Value();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_pm25Value = getCurrentAirCondition(sidoname).getPm25Value();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_so2Value = getCurrentAirCondition(sidoname).getSo2Value();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_coValue = getCurrentAirCondition(sidoname).getCoValue();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_o3Value = getCurrentAirCondition(sidoname).getO3Value();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_no2Value = getCurrentAirCondition(sidoname).getNo2Value();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    current_pm10Grade = getCurrentAirCondition(sidoname).getPm10Grade();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_pm25Grade = getCurrentAirCondition(sidoname).getPm25Grade();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_so2Grade = getCurrentAirCondition(sidoname).getSo2Grade();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_coGrade = getCurrentAirCondition(sidoname).getCoGrade();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_o3Grade = getCurrentAirCondition(sidoname).getO3Grade();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    current_no2Grade = getCurrentAirCondition(sidoname).getNo2Grade();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }

    //RETROFIT 2이용해서  이메일 이용하여 사용자이름 받아노는 메소드
    private void showUserName(String data) {
        service.getName(data).enqueue(new Callback<GetNameResponse>() {
            @Override
            public void onResponse(Call<GetNameResponse> call, Response<GetNameResponse> response) {
                GetNameResponse result = response.body();
                Log.d("message", ""+result.getMessage());

                if(result.getCode() == 200){
                    //~님 환영합니다 라고 써주기
                    name.setText(result.getUsername()+"님 환영합니다");
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

    public void makeToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }



}