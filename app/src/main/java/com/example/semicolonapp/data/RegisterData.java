package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class RegisterData {

    public RegisterData(String userEmail, String userPwd, String userName, String userAge, String guardiannumber) {
        this.userEmail = userEmail;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userAge = userAge;
        this.guardiannumber= guardiannumber;

    }

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userPwd")
    private String userPwd;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userAge")
    private String userAge;

    @SerializedName("guardiannumber")
    private String guardiannumber;


}
