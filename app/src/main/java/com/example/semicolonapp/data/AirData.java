package com.example.semicolonapp.data;

public class AirData {

    String pm10Value;
    String pm25Value;
    String so2Value;
    String coValue;
    String o3Value;
    String no2Value;

    String pm10Grade;
    String pm25Grade;
    String so2Grade;
    String coGrade;
    String o3Grade;
    String no2Grade;

    public AirData(String pm10Value, String pm25Value, String so2Value, String coValue, String o3Value, String no2Value, String pm10Grade, String pm25Grade, String so2Grade, String coGrade, String o3Grade, String no2Grade) {
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
