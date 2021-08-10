package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class LoginData {

    public LoginData(String userEmail, String userPwd) {
        this.userEmail = userEmail;
        this.userPwd = userPwd;
    }

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userPwd")
    private String userPwd;

}
