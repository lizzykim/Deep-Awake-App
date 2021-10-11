package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class GetGuardianNumberResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("guardiannumber")
    private String guardiannumber;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getGuardiannumber() {
        return guardiannumber;
    }
}
