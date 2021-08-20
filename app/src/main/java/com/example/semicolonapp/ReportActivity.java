package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.semicolonapp.adapter.ReportAdapter;
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

        service = RetrofitClient.getClient().create(ServiceApi.class);

        getReportDataFromDB();//reportItems로 db에서 데이터 받는 메서드

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
                //intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    //reportItems로 db에서 데이터 받는 메서드
    //private ArrayList<ReportItemData> getReportDataFromDB() {
    private void getReportDataFromDB() {
        service.getReportRecord().enqueue(new Callback<ReportItemArrayData>() {
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
//                    Log.d("message", ""+result.getTemperature());
//                    Log.d("message", ""+result.getHumidity());

                    for( int i =0 ;i<result.getLat().size() ;i++){
                        data = new ReportItemData();

                        data.setLat(result.getLat().get(i));
                        data.setLon(result.getLon().get(i));
                        data.setDatentime(result.getDatentime().get(i));
                        data.setLocation(result.getLocation().get(i));
                        data.setWeather(result.getWeather().get(i));
                        data.setTemperature(result.getTemperature().get(i));
                        data.setHumidity(result.getHumidity().get(i));
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