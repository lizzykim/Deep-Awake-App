package com.example.semicolonapp.data;

import com.google.gson.annotations.SerializedName;

public class ReportItemData {

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


//    public String city;
//    public String fullName;
//    public int airmise;
//    public int airchomise;
//    public int airno;
//    public int airso2;
//    public int airo3;
//    public int airco;



    public ReportItemData( String datentime, String location,String temperature) {
        this.datentime = datentime;
        this.location = location;
        this.temperature = temperature;
    }

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

//    public ReportItem(String lat, String lon, String datentime, String location, String weather, String temperature, String city, String fullName, int airmise, int airchomise, int airno, int airso2, int airo3, int airco) {
//        this.lat = lat;
//        this.lon = lon;
//        this.datentime = datentime;
//        this.location = location;
//        this.weather = weather;
//        this.temperature = temperature;
////        this.city = city;
////        this.fullName = fullName;
////        this.airmise = airmise;
////        this.airchomise = airchomise;
////        this.airno = airno;
////        this.airso2 = airso2;
////        this.airo3 = airo3;
////        this.airco = airco;
//    }

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
//                ", city='" + city + '\'' +
//                ", fullName='" + fullName + '\'' +
//                ", airmise=" + airmise +
//                ", airchomise=" + airchomise +
//                ", airno=" + airno +
//                ", airso2=" + airso2 +
//                ", airo3=" + airo3 +
//                ", airco=" + airco +
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

//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public int getAirmise() {
//        return airmise;
//    }
//
//    public void setAirmise(int airmise) {
//        this.airmise = airmise;
//    }
//
//    public int getAirchomise() {
//        return airchomise;
//    }
//
//    public void setAirchomise(int airchomise) {
//        this.airchomise = airchomise;
//    }
//
//    public int getAirno() {
//        return airno;
//    }
//
//    public void setAirno(int airno) {
//        this.airno = airno;
//    }
//
//    public int getAirso2() {
//        return airso2;
//    }
//
//    public void setAirso2(int airso2) {
//        this.airso2 = airso2;
//    }
//
//    public int getAiro3() {
//        return airo3;
//    }
//
//    public void setAiro3(int airo3) {
//        this.airo3 = airo3;
//    }
//
//    public int getAirco() {
//        return airco;
//    }
//
//    public void setAirco(int airco) {
//        this.airco = airco;
//    }


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

}
