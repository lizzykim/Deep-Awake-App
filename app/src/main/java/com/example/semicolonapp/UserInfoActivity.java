package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.semicolonapp.data.DataHolder;
import com.example.semicolonapp.data.EditInfoData;
import com.example.semicolonapp.data.GetNameResponse;
import com.example.semicolonapp.data.GetUserInfo;
import com.example.semicolonapp.data.ReportItemResponse;
import com.example.semicolonapp.data.UpdateUserInfoResponse;
import com.example.semicolonapp.network.RetrofitClient;
import com.example.semicolonapp.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity {

    EditText ed_email,ed_name,ed_pw,ed_age;
    Button change;
    private ServiceApi service;//retrofit 관련
    boolean click = false; //초반에 버튼은 누르지 않은 경우('수정'상태)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfoactivity);

        service = RetrofitClient.getClient().create(ServiceApi.class); //RETROFIT 객체

        ed_email = (EditText)findViewById(R.id.user_email);
        ed_name = (EditText)findViewById(R.id.user_name);
        ed_pw = (EditText)findViewById(R.id.user_password);
        ed_age = (EditText)findViewById(R.id.user_age);
        change = (Button)findViewById(R.id.btn_change);


        //이메일은 수정 할 수 없음!!!
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click == false) { //수정-> 수정 완료로 바뀜
                    ed_name.setText(null);
                    ed_pw.setText(null);
                    ed_age.setText(null);
                    change.setText("수정 완료"); //수정 완료 누르면 mysql수정됨
                    click = true;
                } else { //수정완료-> 수정  바뀜
                    changeinfo(new EditInfoData(DataHolder.getUseremail(),ed_pw.getText().toString(),ed_name.getText().toString(),ed_age.getText().toString()));

                    change.setText("수정하기");
                    click = false;
                }
            }
        });

        init(DataHolder.getUseremail());  //초기화 화면으로 해당 사용자 회원 정보가져오기
    }

    private void changeinfo(EditInfoData data) {

        service.updateuserinfo(data).enqueue(new Callback<UpdateUserInfoResponse>() {
            @Override
            public void onResponse(Call<UpdateUserInfoResponse> call, Response<UpdateUserInfoResponse> response) {
                UpdateUserInfoResponse result = response.body();
                Toast.makeText(UserInfoActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UpdateUserInfoResponse> call, Throwable t) {
                Toast.makeText(UserInfoActivity.this, "운전자 회원정보를 수정하지 못했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("운전자 회원정보를 수정하지 못했습니다", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌
            }
        });

    }

    public void init(String data){//해당 사용자 회원 정보가져오기
        service.getuserinfo(data).enqueue(new Callback<GetUserInfo>() {
            @Override
            public void onResponse(Call<GetUserInfo> call, Response<GetUserInfo> response) {
                GetUserInfo result = response.body();
                Log.d("message", "" + result.getMessage());

                if(result.getCode() ==200){
                    ed_email.setText(result.getUseremail());
                    ed_name.setText(result.getUsername());
                    ed_pw.setText(result.getUserpwd());
                    ed_age.setText(result.getUserage());
                }
            }

            @Override
            public void onFailure(Call<GetUserInfo> call, Throwable t) {
                Toast.makeText(UserInfoActivity.this, "사용자 정보를 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
                Log.e("사용자 정보를 불러오지 못했습니다", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌
            }
        });
    }



}