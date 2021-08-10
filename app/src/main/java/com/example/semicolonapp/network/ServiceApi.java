package com.example.semicolonapp.network;

import com.example.semicolonapp.data.GetNameResponse;
import com.example.semicolonapp.data.LoginData;
import com.example.semicolonapp.data.LoginResponse;
import com.example.semicolonapp.data.RegisterData;
import com.example.semicolonapp.data.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceApi {

    @POST("/user/login")
    Call<LoginResponse> userLogin(@Body LoginData data);

    @POST("/user/register")
    Call<RegisterResponse> userRegister(@Body RegisterData data);


     @GET("/user/init")
    Call<GetNameResponse> getName(@Query("userEmail") String data);
}
