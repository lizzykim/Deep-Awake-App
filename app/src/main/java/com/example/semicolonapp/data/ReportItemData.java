package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class ReportItemData {

    //추가
    @SerializedName("useremail")
    public String useremail;

    @SerializedName("lat")
    public String lat;

    @SerializedName("lon")
    public String lon;

    @SerializedName("datentime")
    public String datentime;

    @SerializedName("location")
    public String location;

    @SerializedName("weather")
    public String weather;

    @SerializedName("temperature")
    public String temperature;

    @SerializedName("humidity")
    public String humidity;

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;



    @SerializedName("pm10Value")
    public String pm10Value;

    @SerializedName("pm25Value")
    public String pm25Value;

    @SerializedName("so2Value")
    public String so2Value;

    @SerializedName("coValue")
    public String coValue;

    @SerializedName("o3Value")
    public String o3Value;

    @SerializedName("no2Value")
    public String no2Value;

    @SerializedName("pm10Grade")
    public String pm10Grade;

    @SerializedName("pm25Grade")
    public String pm25Grade;

    @SerializedName("so2Grade")
    public String so2Grade;

    @SerializedName("coGrade")
    public String coGrade;

    @SerializedName("o3Grade")
    public String o3Grade;

    @SerializedName("no2Grade")
    public String no2Grade;



    public ReportItemData() {

    }


    public ReportItemData(String lat, String lon, String datentime, String location, String weather, String temperature , String humidity) {
        this.lat = lat;
        this.lon = lon;
        this.datentime = datentime;
        this.location = location;
        this.weather = weather;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public ReportItemData(String useremail,String lat, String lon, String datentime, String location, String weather, String temperature, String humidity, String pm10Value, String pm25Value, String so2Value, String coValue, String o3Value, String no2Value, String pm10Grade, String pm25Grade, String so2Grade, String coGrade, String o3Grade, String no2Grade) {
        this.useremail = useremail;
        this.lat = lat;
        this.lon = lon;
        this.datentime = datentime;
        this.location = location;
        this.weather = weather;
        this.temperature = temperature;
        this.humidity = humidity;
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



    @Override
    public String toString() {
        return "recorditem={" +
                "lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", datentime='" + datentime + '\'' +
                ", location='" + location + '\'' +
                ", weather='" + weather + '\'' +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity+ '\''+
                ", pm10Value='" + pm10Value + '\'' +
                ", pm25Value='" + pm25Value + '\'' +
                ", so2Value=" + so2Value +
                ", coValue=" + coValue +
                ", no2Value=" + no2Value +
                ", pm10Grade=" + pm10Grade +
                ", pm25Grade=" + pm25Grade+
                ", so2Grade=" + so2Grade +
                ", coGrade=" + coGrade +
                ", o3Grade=" + o3Grade +
                ", no2Grade=" + no2Grade +
                '}';
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDatentime() {
        return datentime;
    }

    public void setDatentime(String datentime) {
        this.datentime = datentime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }


    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
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

    public String getPm10Value() {
        return pm10Value;
    }

    public void setPm10Value(String pm10Value) {
        this.pm10Value = pm10Value;
    }

    public String getPm25Value() {
        return pm25Value;
    }

    public void setPm25Value(String pm25Value) {
        this.pm25Value = pm25Value;
    }

    public String getSo2Value() {
        return so2Value;
    }

    public void setSo2Value(String so2Value) {
        this.so2Value = so2Value;
    }

    public String getCoValue() {
        return coValue;
    }

    public void setCoValue(String coValue) {
        this.coValue = coValue;
    }

    public String getO3Value() {
        return o3Value;
    }

    public void setO3Value(String o3Value) {
        this.o3Value = o3Value;
    }

    public String getNo2Value() {
        return no2Value;
    }

    public void setNo2Value(String no2Value) {
        this.no2Value = no2Value;
    }

    public String getPm10Grade() {
        return pm10Grade;
    }

    public void setPm10Grade(String pm10Grade) {
        this.pm10Grade = pm10Grade;
    }

    public String getPm25Grade() {
        return pm25Grade;
    }

    public void setPm25Grade(String pm25Grade) {
        this.pm25Grade = pm25Grade;
    }

    public String getSo2Grade() {
        return so2Grade;
    }

    public void setSo2Grade(String so2Grade) {
        this.so2Grade = so2Grade;
    }

    public String getCoGrade() {
        return coGrade;
    }

    public void setCoGrade(String coGrade) {
        this.coGrade = coGrade;
    }

    public String getO3Grade() {
        return o3Grade;
    }

    public void setO3Grade(String o3Grade) {
        this.o3Grade = o3Grade;
    }

    public String getNo2Grade() {
        return no2Grade;
    }

    public void setNo2Grade(String no2Grade) {
        this.no2Grade = no2Grade;
    }
}
