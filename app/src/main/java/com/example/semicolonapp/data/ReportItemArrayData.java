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

    @SerializedName("pm10Value")
    public ArrayList<String> pm10Value;

    @SerializedName("pm25Value")
    public ArrayList<String> pm25Value;

    @SerializedName("so2Value")
    public ArrayList<String> so2Value;

    @SerializedName("coValue")
    public ArrayList<String> coValue;

    @SerializedName("o3Value")
    public ArrayList<String> o3Value;

    @SerializedName("no2Value")
    public ArrayList<String> no2Value;

    @SerializedName("pm10Grade")
    public ArrayList<String> pm10Grade;

    @SerializedName("pm25Grade")
    public ArrayList<String> pm25Grade;

    @SerializedName("so2Grade")
    public ArrayList<String> so2Grade;

    @SerializedName("coGrade")
    public ArrayList<String> coGrade;

    @SerializedName("o3Grade")
    public ArrayList<String> o3Grade;

    @SerializedName("no2Grade")
    public ArrayList<String> no2Grade;





    public ReportItemArrayData(ArrayList<String> lat, ArrayList<String> lon, ArrayList<String> datentime, ArrayList<String> location, ArrayList<String> weather, ArrayList<String> temperature, ArrayList<String> humidity) {
        this.lat = lat;
        this.lon = lon;
        this.datentime = datentime;
        this.location = location;
        this.weather = weather;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public ReportItemArrayData(ArrayList<String> lat, ArrayList<String> lon, ArrayList<String> datentime, ArrayList<String> location, ArrayList<String> weather, ArrayList<String> temperature, ArrayList<String> humidity, int code, String message, ArrayList<String> pm10Value, ArrayList<String> pm25Value, ArrayList<String> so2Value, ArrayList<String> coValue, ArrayList<String> o3Value, ArrayList<String> no2Value, ArrayList<String> pm10Grade, ArrayList<String> pm25Grade, ArrayList<String> so2Grade, ArrayList<String> coGrade, ArrayList<String> o3Grade, ArrayList<String> no2Grade) {
        this.lat = lat;
        this.lon = lon;
        this.datentime = datentime;
        this.location = location;
        this.weather = weather;
        this.temperature = temperature;
        this.humidity = humidity;
        this.code = code;
        this.message = message;
        this.pm10Value = pm10Value;
        this.pm25Value = pm25Value;
        this.so2Value = so2Value;
        this.coValue = coValue;
        this.o3Value = o3Value;
        this.no2Value = no2Value;
        this.pm10Grade = pm10Grade;
        this.pm25Grade = pm25Grade;
        this.so2Grade = so2Grade;
        this.coGrade = coGrade;
        this.o3Grade = o3Grade;
        this.no2Grade = no2Grade;
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

    public ArrayList<String> getPm10Value() {
        return pm10Value;
    }

    public void setPm10Value(ArrayList<String> pm10Value) {
        this.pm10Value = pm10Value;
    }

    public ArrayList<String> getPm25Value() {
        return pm25Value;
    }

    public void setPm25Value(ArrayList<String> pm25Value) {
        this.pm25Value = pm25Value;
    }

    public ArrayList<String> getSo2Value() {
        return so2Value;
    }

    public void setSo2Value(ArrayList<String> so2Value) {
        this.so2Value = so2Value;
    }

    public ArrayList<String> getCoValue() {
        return coValue;
    }

    public void setCoValue(ArrayList<String> coValue) {
        this.coValue = coValue;
    }

    public ArrayList<String> getO3Value() {
        return o3Value;
    }

    public void setO3Value(ArrayList<String> o3Value) {
        this.o3Value = o3Value;
    }

    public ArrayList<String> getNo2Value() {
        return no2Value;
    }

    public void setNo2Value(ArrayList<String> no2Value) {
        this.no2Value = no2Value;
    }

    public ArrayList<String> getPm10Grade() {
        return pm10Grade;
    }

    public void setPm10Grade(ArrayList<String> pm10Grade) {
        this.pm10Grade = pm10Grade;
    }

    public ArrayList<String> getPm25Grade() {
        return pm25Grade;
    }

    public void setPm25Grade(ArrayList<String> pm25Grade) {
        this.pm25Grade = pm25Grade;
    }

    public ArrayList<String> getSo2Grade() {
        return so2Grade;
    }

    public void setSo2Grade(ArrayList<String> so2Grade) {
        this.so2Grade = so2Grade;
    }

    public ArrayList<String> getCoGrade() {
        return coGrade;
    }

    public void setCoGrade(ArrayList<String> coGrade) {
        this.coGrade = coGrade;
    }

    public ArrayList<String> getO3Grade() {
        return o3Grade;
    }

    public void setO3Grade(ArrayList<String> o3Grade) {
        this.o3Grade = o3Grade;
    }

    public ArrayList<String> getNo2Grade() {
        return no2Grade;
    }

    public void setNo2Grade(ArrayList<String> no2Grade) {
        this.no2Grade = no2Grade;
    }
}
