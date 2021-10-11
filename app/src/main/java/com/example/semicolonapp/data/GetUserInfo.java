package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class GetUserInfo {

    public GetUserInfo(String useremail, String userpwd, String username, String userage) {
        this.useremail = useremail;
        this.userpwd = userpwd;
        this.username = username;
        this.userage = userage;
    }

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("useremail")
    private String useremail;

    @SerializedName("userpwd")
    private String userpwd;

    @SerializedName("username")
    private String username;

    @SerializedName("userage")
    private String userage;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserpwd() {
        return userpwd;
    }

    public void setUserpwd(String userpwd) {
        this.userpwd = userpwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserage() {
        return userage;
    }

    public void setUserage(String userage) {
        this.userage = userage;
    }
}
