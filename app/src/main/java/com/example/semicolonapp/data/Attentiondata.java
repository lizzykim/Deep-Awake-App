package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class Attentiondata {

    @SerializedName("ATTENTION")
    public int attenton;

    public Attentiondata(int attenton) {
        this.attenton = attenton;
    }

    public int getAttenton() {
        return attenton;
    }

    public void setAttenton(int attenton) {
        this.attenton = attenton;
    }
}
