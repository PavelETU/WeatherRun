package com.example.pavelsuvit.weatherapplication.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pavelsuvit.weatherapplication.dataPresenters.DetailedWeatherData;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabase;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabaseContract.CurrentWeatherEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class loads weather data if internet connection is not established
 */

public class LoadDataFromDatabaseTask extends AsyncTaskLoader<List<DetailedWeatherData>> {
    private SQLiteOpenHelper databaseHelper;
    public LoadDataFromDatabaseTask(Context context) {
        super(context);
        databaseHelper = new WeatherDatabase(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<DetailedWeatherData> loadInBackground() {
        List<DetailedWeatherData> result = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.query(CurrentWeatherEntry.TABLE_NAME,
                    null, null, null, null, null, null);
            //int indexId = cursor.getColumnIndex(CurrentWeatherEntry._ID);
            int indexCityName = cursor.getColumnIndex(CurrentWeatherEntry.CITY_NAME);
            int indexCurrentWeather = cursor.getColumnIndex(CurrentWeatherEntry.CURRENT_WEATHER);
            int indexHumidity = cursor.getColumnIndex(CurrentWeatherEntry.HUMIDITY);
            int indexIconNumber = cursor.getColumnIndex(CurrentWeatherEntry.ICON_NUMBER);
            int indexTime = cursor.getColumnIndex(CurrentWeatherEntry.TIME);
            int indexTimeZone = cursor.getColumnIndex(CurrentWeatherEntry.TIME_ZONE);
            int indexPressure = cursor.getColumnIndex(CurrentWeatherEntry.PRESSURE);
            int indexWindSpeed = cursor.getColumnIndex(CurrentWeatherEntry.WIND_SPEED);
            while (cursor.moveToNext()) {
                String cityName = cursor.getString(indexCityName);
                int currentWeather = cursor.getInt(indexCurrentWeather);
                String iconNumber = cursor.getString(indexIconNumber);
                long time = cursor.getLong(indexTime);
                String timeZone = cursor.getString(indexTimeZone);
                int pressure = cursor.getInt(indexPressure);
                int humidity = cursor.getInt(indexHumidity);
                int windSpeed = cursor.getInt(indexWindSpeed);
                result.add(new DetailedWeatherData(cityName, currentWeather,
                        iconNumber, time, timeZone, pressure, humidity, windSpeed));
            }
            cursor.close();
        } finally {
            if (databaseHelper != null) {
                databaseHelper.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return result;
    }
}
