package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class GuardianNumber {

    @SerializedName("GuardianNumber")
    public String guardiannumber;

    public GuardianNumber(String guardiannumber) {
        this.guardiannumber = guardiannumber;
    }

    public String getGuardiannumber() {
        return guardiannumber;
    }

    public void setGuardiannumber(String guardiannumber) {
        this.guardiannumber = guardiannumber;
    }
}
