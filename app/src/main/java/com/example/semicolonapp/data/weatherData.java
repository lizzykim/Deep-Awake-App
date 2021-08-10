package com.example.semicolonapp.data;

public class weatherData {
    String currentTemp;
    String currentHumidity;
    String currentWeather;

    public weatherData(String currentTemp, String currentHumidity, String currentWeather) {
        this.currentTemp = currentTemp;
        this.currentHumidity = currentHumidity;
        this.currentWeather = currentWeather;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String getCurrentHumidity() {
        return currentHumidity;
    }

    public void setCurrentHumidity(String currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    public String getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(String currentWeather) {
        this.currentWeather = currentWeather;
    }
}
