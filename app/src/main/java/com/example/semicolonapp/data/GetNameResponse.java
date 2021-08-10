package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class GetNameResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("username")
    private String username;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
