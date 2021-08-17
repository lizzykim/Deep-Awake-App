package com.example.semicolonapp.network;

import com.example.semicolonapp.data.GetNameResponse;
import com.example.semicolonapp.data.LoginData;
import com.example.semicolonapp.data.LoginResponse;
import com.example.semicolonapp.data.RegisterData;
import com.example.semicolonapp.data.RegisterResponse;
import com.example.semicolonapp.data.ReportItemArrayData;
import com.example.semicolonapp.data.ReportItemData;
import com.example.semicolonapp.data.ReportItemResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceApi {

    @POST("/user/login") // 로그인
    Call<LoginResponse> userLogin(@Body LoginData data);

    @POST("/user/register") //이메일, 비번 가입
    Call<RegisterResponse> userRegister(@Body RegisterData data);

     @GET("/user/init") //사용자 "이름" 가져와서 main화면에 띄어주는 것
    Call<GetNameResponse> getName(@Query("userEmail") String data);

    @POST("/user/reportrecord") //개개인의 운전 레코드 저장
    Call<ReportItemResponse> postReportRecord(@Body ReportItemData data);

    @GET("/user/reportrecord") //운전자 운전레코드 가져와서 listview로 뿌려주는 것
    Call<ReportItemArrayData> getReportRecord();

}
