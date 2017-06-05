package com.example.pavelsuvit.weatherapplication.dataPresenters;


import android.provider.BaseColumns;

public class WeatherDatabaseContract {
    private WeatherDatabaseContract() {}
    // We have two tables in database for keeping weather.
    // In forecast table we keep track corresponding row in currentWeather table
    // and in currentWeather table we keep cityId - we will make http request based on those values.
    private static class CommonWeatherDataEntry implements BaseColumns {
        public static final String CITY_NAME = "cityName";
        public static final String ICON_NUMBER = "iconNumber";
        public static final String CURRENT_WEATHER = "currentWeather";
        public static final String TIME = "time";
        public static final String PRESSURE = "pressure";
        public static final String HUMIDITY = "humidity";
        public static final String WIND_SPEED = "windSpeed";
        public static final String TIME_ZONE = "timeZone";
    }
    public static class CurrentWeatherEntry extends CommonWeatherDataEntry {
        public static final String TABLE_NAME = "currentWeather";
        public static final String CITY_ID = "cityId";
    }

    public static class ForecastEntry extends CommonWeatherDataEntry {
        public static final String TABLE_NAME = "forecast";
        public static final String CORRESPONDING_ROW_ID_IN_CURRENT_WEATHER_TABLE = "correspondingRowInCurrentWeather";
    }
}
