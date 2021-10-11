package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.semicolonapp.adapter.ReportAdapter;
import com.example.semicolonapp.data.DataHolder;
import com.example.semicolonapp.data.GetNameResponse;
import com.example.semicolonapp.data.ReportItemArrayData;
import com.example.semicolonapp.data.ReportItemData;
import com.example.semicolonapp.data.ReportItemResponse;
import com.example.semicolonapp.network.RetrofitClient;
import com.example.semicolonapp.network.ServiceApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private static final String TAG ="ReportActivity";

    ListView lv_view;
    TextView username; //사용자 이름
    String useremail ="";

    ReportItemData data;
    ArrayList<ReportItemData> ItemsArrays= new ArrayList<ReportItemData>();
    ReportAdapter adapter;

    private ServiceApi service;//retrofit 관련


    protected void onCreate(Bundle savedInstanceState) {

        //ReportFragment 에서 adapter.get날짜, adpater.get경위도 로 전달 받은 데이터에 접근


        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportactivity);



        username=(TextView)findViewById(R.id.rp_user_name); //사용자 이름 참조
        lv_view = (ListView)findViewById(R.id.listView); //listview 참조


        //listview 에 부착할 baseadapter 상속받은 ReportAdapter객체 생성
//        ItemsArrays.add(new ReportItemData("8월 8일","서울","36"));
//        ItemsArrays.add(new ReportItemData("8월 9일","부산","32"));
//        ItemsArrays.add(new ReportItemData("8월 10일","여수","33"));
//        ItemsArrays.add(new ReportItemData("8월 11일","강릉","32"));


        Log.i("DataHolder", DataHolder.getUseremail());
        service = RetrofitClient.getClient().create(ServiceApi.class);


        showUserName(DataHolder.getUseremail()); //메인 화면 들어올때 username SQL,RETROFIT2 으로 받아오는 코드, 사용자 이름 보여줌//

        //getReportDataFromDB();//reportItems로 db에서 데이터 받는 메서드
        getReportDataFromDB(DataHolder.getUseremail());//reportItems로 db에서 데이터 받는 메서드

        adapter = new ReportAdapter(this,ItemsArrays);
        lv_view.setAdapter(adapter);

        //listview의 list클릭했을때 -> 클릭 이벤트 핸들러 정의
        lv_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position  == 내가 listview에서 터치한 인덱스
                Toast.makeText(ReportActivity.this ,ItemsArrays.get(position).getDatentime(),Toast.LENGTH_SHORT).show();//날자로 toast띄우기
                Intent intent = new Intent(ReportActivity.this,ReportDetailActivity.class);
                //날짜,주소,날씨,온도,위경도 다 같이 넘겨주기
                intent.putExtra("date",ItemsArrays.get(position).getDatentime());
                intent.putExtra("address",ItemsArrays.get(position).getLocation());
                intent.putExtra("weather",ItemsArrays.get(position).getWeather());
                intent.putExtra("temperature",ItemsArrays.get(position).getTemperature());
                intent.putExtra("humidity",ItemsArrays.get(position).getHumidity());
                intent.putExtra("lat",ItemsArrays.get(position).getLat());
                intent.putExtra("long",ItemsArrays.get(position).getLon());

                intent.putExtra("pm10Value",ItemsArrays.get(position).getPm10Value());
                intent.putExtra("pm25Value",ItemsArrays.get(position).getPm25Value());
                intent.putExtra("so2Value",ItemsArrays.get(position).getSo2Value());
                intent.putExtra("coValue",ItemsArrays.get(position).getCoValue());
                intent.putExtra("o3Value",ItemsArrays.get(position).getO3Value());
                intent.putExtra("no2Value",ItemsArrays.get(position).getNo2Value());
                intent.putExtra("pm10Grade",ItemsArrays.get(position).getPm10Grade());
                intent.putExtra("pm25Grade",ItemsArrays.get(position).getPm25Grade());
                intent.putExtra("so2Grade",ItemsArrays.get(position).getSo2Grade());
                intent.putExtra("coGrade",ItemsArrays.get(position).getCoGrade());
                intent.putExtra("o3Grade",ItemsArrays.get(position).getO3Grade());
                intent.putExtra("no2Grade",ItemsArrays.get(position).getNo2Grade());



                //intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

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
                    username.setText(result.getUsername()+"님 운전 리포트");
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


    //reportItems로 db에서 데이터 받는 메서드

//    private void getReportDataFromDB() {
//        service.getReportRecord().enqueue(new Callback<ReportItemArrayData>() {
            private void getReportDataFromDB(String useremail) {
                service.getReportRecord(useremail).enqueue(new Callback<ReportItemArrayData>() {
            @Override
            public void onResponse(Call<ReportItemArrayData> call, Response<ReportItemArrayData> response) {
                //nodejs 하고 작성하기
                ReportItemArrayData result = response.body();

                if(result.getCode()== 200){

//                    Log.d("message", ""+result.getMessage());
//                    Log.d("message", ""+result.getLat());
//                    Log.d("message", ""+result.getLon());
//                    Log.d("message", ""+result.getDatentime());
//                    Log.d("message", ""+result.getLocation());
//                    Log.d("message", ""+result.getWeather());
//                    Log.d("message", ""+result.getPm10Value());
//                    Log.d("message", ""+result.getPm10Grade());

                    for( int i =0 ;i<result.getLat().size() ;i++){ //null에러뜸
                        data = new ReportItemData();

                        data.setLat(result.getLat().get(i));
                        data.setLon(result.getLon().get(i));
                        data.setDatentime(result.getDatentime().get(i));
                        data.setLocation(result.getLocation().get(i));
                        data.setWeather(result.getWeather().get(i));
                        data.setTemperature(result.getTemperature().get(i));
                        data.setHumidity(result.getHumidity().get(i));

                        data.setPm10Value(result.getPm10Value().get(i));
                        data.setPm25Value(result.getPm25Value().get(i));
                        data.setSo2Value(result.getSo2Value().get(i));
                        data.setCoValue(result.getCoValue().get(i));
                        data.setO3Value(result.getO3Value().get(i));
                        data.setNo2Value(result.getNo2Value().get(i));
                        data.setPm10Grade(result.getPm10Grade().get(i));
                        data.setPm25Grade(result.getPm25Grade().get(i));
                        data.setSo2Grade(result.getSo2Grade().get(i));
                        data.setCoGrade(result.getCoGrade().get(i));
                        data.setO3Grade(result.getO3Grade().get(i));
                        data.setNo2Grade(result.getNo2Grade().get(i));

                        ItemsArrays.add(data);

                        //Log.d("messageloop", ""+result.getLat().get(i));
                        Log.d("data", ""+data);
                    }

                    Log.d("ItemsArrays", ""+ItemsArrays);
//                    adapter.setData(ItemsArrays);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onFailure(Call<ReportItemArrayData> call, Throwable t) {
                Log.e("사용자 운전레코드 가져오기 에러 발생", t.getMessage());
                t.printStackTrace();
            }
        });

        //return reportItemsData;
    }
}