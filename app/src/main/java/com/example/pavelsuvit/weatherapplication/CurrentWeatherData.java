package com.example.pavelsuvit.weatherapplication;

/**
 * Created by pavel.suvit on 19.04.2017.
 */

public class CurrentWeatherData {
    private String iconNumber;
    private int currentWeather;
    private String city;
    public CurrentWeatherData(String city, int currentWeather, String iconNumber) {
        this.iconNumber = iconNumber;
        this.currentWeather = currentWeather;
        this.city = city;
    }

    public String getIconNumber() {
        return iconNumber;
    }

    public int getCurrentWeather() {
        return currentWeather;
    }

    public String getCity() {
        return city;
    }
}
