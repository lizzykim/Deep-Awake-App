package com.example.semicolonapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semicolonapp.R;

import java.util.ArrayList;

public class HelperAdapter extends RecyclerView.Adapter<HelperAdapter.MyViewClass> {

    ArrayList<String> name;
    ArrayList<String> road_type;
    ArrayList<String> road_route_name;
    ArrayList<Float> latitude;
    ArrayList<Float> longitude;
    ArrayList<String> gas_station;
    ArrayList<String> lpg_charge;
    ArrayList<String> electric_charge;
    ArrayList<String> phone;
    Context context;

    public HelperAdapter(ArrayList<String> name, ArrayList<String> road_type, ArrayList<String> road_route_name, ArrayList<Float> latitude, ArrayList<Float> longitude, ArrayList<String> gas_station, ArrayList<String> lpg_charge, ArrayList<String> electric_charge, ArrayList<String> phone, Context context) {
        this.name = name;
        this.road_type = road_type;
        this.road_route_name = road_route_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gas_station = gas_station;
        this.lpg_charge = lpg_charge;
        this.electric_charge = electric_charge;
        this.phone = phone;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        MyViewClass myViewClass = new MyViewClass(view);
        return myViewClass;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewClass holder, int position) {
        holder.name.setText("휴게소명: "+name.get(position));
        holder.road_type.setText("도로종류: "+road_type.get(position));
        holder.road_route_name.setText("도로노선명: "+road_route_name.get(position));
        holder.latitude.setText("위도: "+latitude.get(position).toString());
        holder.longitude.setText("경도: "+longitude.get(position).toString());
        holder.gas_station.setText("주유소유무: "+gas_station.get(position));
        holder.lpg_charge.setText("LPG충전소유무: "+lpg_charge.get(position));
        holder.electric_charge.setText("전기차충전소유무: "+electric_charge.get(position));
        holder.phone.setText("전기차충전소유무: "+phone.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Item Clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class MyViewClass extends RecyclerView.ViewHolder {

        TextView name;
        TextView road_type;
        TextView road_route_name;
        TextView latitude;
        TextView longitude;
        TextView gas_station;
        TextView lpg_charge;
        TextView electric_charge;
        TextView phone;

        public MyViewClass(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            road_type = (TextView) itemView.findViewById(R.id.road_type);
            road_route_name = (TextView) itemView.findViewById(R.id.road_route_name);
            latitude = (TextView) itemView.findViewById(R.id.latitude);
            longitude = (TextView) itemView.findViewById(R.id.longitude);
            gas_station = (TextView) itemView.findViewById(R.id.gas_station);
            lpg_charge = (TextView) itemView.findViewById(R.id.lpg_charge);
            electric_charge = (TextView) itemView.findViewById(R.id.electric_charge);
            phone = (TextView) itemView.findViewById(R.id.phone);


        }
    }


}
