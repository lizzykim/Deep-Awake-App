package com.example.semicolonapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.semicolonapp.R;
import com.example.semicolonapp.data.ReportItemArrayData;
import com.example.semicolonapp.data.ReportItemData;

import java.util.ArrayList;

public class ReportAdapter extends BaseAdapter {

    public static final String TAG = "ReportAdapter";
    //adapter에 추가된 데이터를 저장하기 위한 ReportItem형의 arraylist
    ArrayList<ReportItemData> reportItems;
    private Context context;


    //생성자
    public ReportAdapter(Context context, ArrayList<ReportItemData> data) {
        this.context = context;
        this.reportItems = data;

    }


    //adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return reportItems.size();
    }

    //지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return reportItems.get(position);
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    //reportitem 에 들어가는 view를 저장해주는 클래스
    class ViewHolder{
        ImageView iv_weather;
        TextView tv_datentime;
        TextView tv_fullName;
        TextView tv_temperature;

    }

    //position에 위치한 데이터를 화면에 출력하는데 사용된 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        final Context context = parent.getContext(); //이상하면 앞에 final 붙여줄것

        if(convertView == null){ //리턴하려는 view가 없을때
            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.reportitem,null);
            holder = new ViewHolder();
            holder.iv_weather = (ImageView) convertView.findViewById(R.id.iv_weather);
            holder.tv_temperature=(TextView)convertView.findViewById(R.id.tv_temperature);
            holder.tv_fullName = (TextView)convertView.findViewById(R.id.tv_fullName);
            holder.tv_datentime=(TextView)convertView.findViewById(R.id.tv_datentime);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ReportItemData report_item = reportItems.get(position);


        Log.i(TAG, "reportadapter의 현재시간:" +report_item.getDatentime());

        holder.tv_temperature.setText(report_item.getTemperature()+"ºC"); //온도
        holder.tv_datentime.setText(report_item.getDatentime()); //날짜
        holder.tv_fullName.setText(report_item.getLocation()); //지역이름
        //convertView.setOnClickListener();

        return convertView;
    }


    //ReportITem의 배열에 데이터 추가(지정) reportActivity에서서
    public void setData(ArrayList<ReportItemData> data){
        for ( int i=0 ;i <data.size(); i++){
            ReportItemData rp_item = data.get(i);
            this.reportItems.add(rp_item); //reportItems 에 운전관련 데이터를 저장해주는 것
        }
    }



//    public void setData(ArrayList<ReportItemArrayData> data){
//        for ( int i=0 ;i <data.size(); i++){
//            ReportItemArrayData rp_item = data.get(i);
//            this.reportItemsData.add(rp_item); //reportItems 에 운전관련 데이터를 저장해주는 것
//        }
//    }
}
