package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class EditInfoData {

    public EditInfoData(String useremail, String userpwd, String username, String userage) {
        this.useremail = useremail;
        this.userpwd = userpwd;
        this.username = username;
        this.userage = userage;
    }

    @SerializedName("useremail")
    private String useremail;

    @SerializedName("userpwd")
    private String userpwd;

    @SerializedName("username")
    private String username;

    @SerializedName("userage")
    private String userage;

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
