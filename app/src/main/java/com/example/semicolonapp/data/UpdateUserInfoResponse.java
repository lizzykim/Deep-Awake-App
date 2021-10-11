package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class UpdateUserInfoResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
