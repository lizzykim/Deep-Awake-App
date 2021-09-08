package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class Meditationdata {

    @SerializedName("MEDITATION")
    public int meditation;

    public Meditationdata(int meditation) {
        this.meditation = meditation;
    }

    public int getMeditation() {
        return meditation;
    }

    public void setMeditation(int meditation) {
        this.meditation = meditation;
    }

}
