package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class RegisterData {

    public RegisterData(String userEmail, String userPwd, String userName, String userAge) {
        this.userEmail = userEmail;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userAge = userAge;
    }

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userPwd")
    private String userPwd;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userAge")
    private String userAge;


}
