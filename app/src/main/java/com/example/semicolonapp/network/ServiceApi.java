package com.example.semicolonapp.network;

import com.example.semicolonapp.data.Attentiondata;
import com.example.semicolonapp.data.EEGdata;
import com.example.semicolonapp.data.GetNameResponse;
import com.example.semicolonapp.data.LoginData;
import com.example.semicolonapp.data.LoginResponse;
import com.example.semicolonapp.data.Meditationdata;
import com.example.semicolonapp.data.RAWdata;
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

    @POST("/user/eeg/train_raw") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    //Call<RAWTrainResponse> post_RAW_train(@Body String data);
    //Call<Void> post_RAW_train(@Body String data);
    Call<Void> post_RAW_train(@Body RAWdata data);

    @POST("/user/eeg/train_eeg") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    Call<Void> post_EEG_train(@Body EEGdata data);

    @POST("/user/eeg/attention") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    Call<Void> post_ATTENTION_train(@Body Attentiondata data);

    @POST("/user/eeg/meditation") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    Call<Void> post_MEDITATION_train(@Body Meditationdata data);

//    @POST("https://0nmhkvy9nf.execute-api.ap-northeast-2.amazonaws.com/semicolon")
//    Call<Void> post_Lambda_MEDITATIOM_train(@Body Meditationdata data);


}
