package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class RAWdata {

    @SerializedName("RAW")
    public String raw;

    public RAWdata() {
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
