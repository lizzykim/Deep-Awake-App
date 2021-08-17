package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReportItemArrayData {

    @SerializedName("lat")
    public ArrayList<String> lat;

    @SerializedName("lon")
    public ArrayList<String> lon;

    @SerializedName("datentime")
    public ArrayList<String> datentime;

    @SerializedName("location")
    public ArrayList<String> location;

    @SerializedName("weather")
    public ArrayList<String> weather;

    @SerializedName("temperature")
    public ArrayList<String> temperature;

    @SerializedName("humidity")
    public ArrayList<String> humidity;

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    public ReportItemArrayData(ArrayList<String> lat, ArrayList<String> lon, ArrayList<String> datentime, ArrayList<String> location, ArrayList<String> weather, ArrayList<String> temperature, ArrayList<String> humidity) {
        this.lat = lat;
        this.lon = lon;
        this.datentime = datentime;
        this.location = location;
        this.weather = weather;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public ArrayList<String> getLat() {
        return lat;
    }

    public void setLat(ArrayList<String> lat) {
        this.lat = lat;
    }

    public ArrayList<String> getLon() {
        return lon;
    }

    public void setLon(ArrayList<String> lon) {
        this.lon = lon;
    }

    public ArrayList<String> getDatentime() {
        return datentime;
    }

    public void setDatentime(ArrayList<String> datentime) {
        this.datentime = datentime;
    }

    public ArrayList<String> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<String> location) {
        this.location = location;
    }

    public ArrayList<String> getWeather() {
        return weather;
    }

    public void setWeather(ArrayList<String> weather) {
        this.weather = weather;
    }

    public ArrayList<String> getTemperature() {
        return temperature;
    }

    public void setTemperature(ArrayList<String> temperature) {
        this.temperature = temperature;
    }

    public ArrayList<String> getHumidity() {
        return humidity;
    }

    public void setHumidity(ArrayList<String> humidity) {
        this.humidity = humidity;
    }

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
}
