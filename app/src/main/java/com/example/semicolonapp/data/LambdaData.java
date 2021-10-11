package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class LambdaData {

    @SerializedName("EEG_delta")
    public int eeg_delta;

    @SerializedName("EEG_theta")
    public int eeg_theta;

    @SerializedName("EEG_lowAlpha")
    public int eeg_lowAlpha;

    @SerializedName("EEG_highAlpha")
    public int eeg_highAlpha;

    @SerializedName("EEG_lowBeta")
    public int eeg_lowBeta;

    @SerializedName("EEG_highBeta")
    public int eeg_highBeta;

    @SerializedName("EEG_lowGamma")
    public int eeg_lowGamma;

    @SerializedName("EEG_midGamma")
    public int eeg_midGamma;

    @SerializedName("MEDITATION")
    public int meditation;

    @SerializedName("ATTENTION")
    public int attenton;


    public LambdaData(int eeg_delta, int eeg_theta, int eeg_lowAlpha, int eeg_highAlpha, int eeg_lowBeta, int eeg_highBeta, int eeg_lowGamma, int eeg_midGamma, int attenton, int meditation) {
        this.eeg_delta = eeg_delta;
        this.eeg_theta = eeg_theta;
        this.eeg_lowAlpha = eeg_lowAlpha;
        this.eeg_highAlpha = eeg_highAlpha;
        this.eeg_lowBeta = eeg_lowBeta;
        this.eeg_highBeta = eeg_highBeta;
        this.eeg_lowGamma = eeg_lowGamma;
        this.eeg_midGamma = eeg_midGamma;
        this.attenton = attenton;
        this.meditation = meditation;

    }




    public int getEeg_delta() {
        return eeg_delta;
    }

    public void setEeg_delta(int eeg_delta) {
        this.eeg_delta = eeg_delta;
    }

    public int getEeg_theta() {
        return eeg_theta;
    }

    public void setEeg_theta(int eeg_theta) {
        this.eeg_theta = eeg_theta;
    }

    public int getEeg_lowAlpha() {
        return eeg_lowAlpha;
    }

    public void setEeg_lowAlpha(int eeg_lowAlpha) {
        this.eeg_lowAlpha = eeg_lowAlpha;
    }

    public int getEeg_highAlpha() {
        return eeg_highAlpha;
    }

    public void setEeg_highAlpha(int eeg_highAlpha) {
        this.eeg_highAlpha = eeg_highAlpha;
    }

    public int getEeg_lowBeta() {
        return eeg_lowBeta;
    }

    public void setEeg_lowBeta(int eeg_lowBeta) {
        this.eeg_lowBeta = eeg_lowBeta;
    }

    public int getEeg_highBeta() {
        return eeg_highBeta;
    }

    public void setEeg_highBeta(int eeg_highBeta) {
        this.eeg_highBeta = eeg_highBeta;
    }

    public int getEeg_lowGamma() {
        return eeg_lowGamma;
    }

    public void setEeg_lowGamma(int eeg_lowGamma) {
        this.eeg_lowGamma = eeg_lowGamma;
    }

    public int getEeg_midGamma() {
        return eeg_midGamma;
    }

    public void setEeg_midGamma(int eeg_midGamma) {
        this.eeg_midGamma = eeg_midGamma;
    }

    public int getMeditation() {
        return meditation;
    }

    public void setMeditation(int meditation) {
        this.meditation = meditation;
    }

    public int getAttenton() {
        return attenton;
    }

    public void setAttenton(int attenton) {
        this.attenton = attenton;
    }
}
