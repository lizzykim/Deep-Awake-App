package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.semicolonapp.adapter.ReportAdapter;
import com.example.semicolonapp.data.ReportItem;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    private static final String TAG ="ReportActivity";

    ListView lv_view;
    TextView username; //사용자 이름

    ArrayList<ReportItem> reportItems=  new ArrayList<ReportItem>();
    ReportAdapter adapter;



    protected void onCreate(Bundle savedInstanceState) {

        //ReportFragment 에서 adapter.get날짜, adpater.get경위도 로 전달 받은 데이터에 접근


        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportactivity);

        username=(TextView)findViewById(R.id.rp_user_name); //사용자 이름 참조
        lv_view = (ListView)findViewById(R.id.listView); //listview 참조


        //listview 에 부착할 baseadapter 상속받은 ReportAdapter객체 생성
//        reportItems.add(new ReportItem("8월 8일","서울","36"));
//        reportItems.add(new ReportItem("8월 9일","부산","32"));
//        reportItems.add(new ReportItem("8월 10일","여수","33"));
//        reportItems.add(new ReportItem("8월 11일","강릉","32"));

        adapter = new ReportAdapter(this,reportItems);
        lv_view.setAdapter(adapter);

        //listview의 list클릭했을때 -> 클릭 이벤트 핸들러 정의
        lv_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //position  == 내가 listview에서 터치한 인덱스
                Toast.makeText(ReportActivity.this ,reportItems.get(position).getDatentime(),Toast.LENGTH_SHORT).show();//날자로 toast띄우기
            }
        });

    }
}