package com.example.semicolonapp.network;

import com.example.semicolonapp.data.Attentiondata;
import com.example.semicolonapp.data.Blinkdata;
import com.example.semicolonapp.data.EEGdata;
import com.example.semicolonapp.data.EditInfoData;
import com.example.semicolonapp.data.GetGuardianNumberResponse;
import com.example.semicolonapp.data.GetNameResponse;
import com.example.semicolonapp.data.GetUserInfo;
import com.example.semicolonapp.data.GuardianNumber;
import com.example.semicolonapp.data.LoginData;
import com.example.semicolonapp.data.LoginResponse;
import com.example.semicolonapp.data.Meditationdata;
import com.example.semicolonapp.data.PostGuardianNumberResponse;
import com.example.semicolonapp.data.RAWdata;
import com.example.semicolonapp.data.RegisterData;
import com.example.semicolonapp.data.RegisterResponse;
import com.example.semicolonapp.data.ReportItemArrayData;
import com.example.semicolonapp.data.ReportItemData;
import com.example.semicolonapp.data.ReportItemResponse;
import com.example.semicolonapp.data.UpdateUserInfoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ServiceApi {

    @POST("/user/login") // 로그인
    Call<LoginResponse> userLogin(@Body LoginData data);

    @POST("/user/register") //이메일, 비번 , 이름 , 나이, 보호자 번호 등록
    Call<RegisterResponse> userRegister(@Body RegisterData data);

     @GET("/user/init") //사용자 "이름" 가져와서 main화면에 띄어주는 것
    Call<GetNameResponse> getName(@Query("userEmail") String data);

    @POST("/user/reportrecord") //개개인의 운전 레코드 저장
    Call<ReportItemResponse> postReportRecord(@Body ReportItemData data);

    @GET("/user/reportrecord") //운전자 운전레코드 가져와서 listview로 뿌려주는 것
    Call<ReportItemArrayData> getReportRecord(@Query("useremail") String useremail);

    @POST("/user/eeg/train_raw") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    Call<Void> post_RAW_train(@Body RAWdata data);

    @POST("/user/eeg/train_eeg") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    Call<Void> post_EEG_train(@Body EEGdata data);

    @POST("/user/eeg/attention") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    Call<Void> post_ATTENTION_train(@Body Attentiondata data);

    @POST("/user/eeg/meditation") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    Call<Void> post_MEDITATION_train(@Body Meditationdata data);

    @POST("/user/eeg/blink") //운전자 뇌파 학습용 데이터 서버에 전달(임시 => nodejs/ 추후 aws 람다)
    Call<Void> post_BLINK_train(@Body Blinkdata data);

    @GET("/user/getsettinginfo") //회원 정보 들어가면 사용자 정보 출력
    Call<GetUserInfo> getuserinfo(@Query("USEREMAIL") String data);

    @PUT("/user/updateuserinfo")//회원 정보 수정
    Call<UpdateUserInfoResponse> updateuserinfo(@Body EditInfoData data);

    @GET("/user/guardiannumber")//사용자 이메일로 "보호자 번호 " 가져오기
    Call<GetGuardianNumberResponse>getGuardianNumber(@Query("USEREMAIL") String useremail);

    @POST("/user/guardiannumber")// "보호자 번호 " 저장하기
    Call <PostGuardianNumberResponse>postGuardianNumber(@Query("USEREMAIL") String useremail, @Body GuardianNumber data);




}
