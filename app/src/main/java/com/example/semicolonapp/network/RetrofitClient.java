package com.example.semicolonapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //private final static String BASE_URL = "http://192.168.0.2:3000"; //
    //private final static String BASE_URL = "http://192.168.219.118:3000"; //외부 ip(탐탐 카폐)
    private final static String BASE_URL = "http://ec2-3-38-104-11.ap-northeast-2.compute.amazonaws.com:3000";


    private static Retrofit retrofit = null;

    public RetrofitClient() {
    }

    public static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }


}
