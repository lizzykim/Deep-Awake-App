package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class Blinkdata {

    @SerializedName("BLINK")
    public int blink;

    public Blinkdata(int blink) {
        this.blink = blink;
    }

    public int getBlink() {
        return blink;
    }

    public void setBlink(int blink) {
        this.blink = blink;
    }
}
