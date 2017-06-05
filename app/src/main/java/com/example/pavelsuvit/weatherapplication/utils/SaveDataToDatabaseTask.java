package com.example.pavelsuvit.weatherapplication.utils;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pavelsuvit.weatherapplication.dataPresenters.DetailedWeatherData;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabase;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabaseContract.CurrentWeatherEntry;

import java.util.List;

/**
 * This AsyncTaskLoader will save weather data to the database
 */

public class SaveDataToDatabaseTask extends AsyncTaskLoader<Boolean> {

    private List<DetailedWeatherData> weatherDataList;
    SQLiteOpenHelper databaseHelper;

    public SaveDataToDatabaseTask(Context context, List<DetailedWeatherData> weatherDataList) {
        super(context);
        this.weatherDataList = weatherDataList;
        databaseHelper = new WeatherDatabase(context);
    }

    @Override
    public Boolean loadInBackground() {
        SQLiteDatabase database = null;
        try {
            database = databaseHelper.getWritableDatabase();
            for (int i = 0; i < weatherDataList.size(); i++) {
                DetailedWeatherData weatherObject = weatherDataList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(CurrentWeatherEntry.CITY_NAME, weatherObject.getCity());
                contentValues.put(CurrentWeatherEntry.CURRENT_WEATHER, weatherObject.getCurrentWeather());
                contentValues.put(CurrentWeatherEntry.HUMIDITY, weatherObject.getHumidity());
                contentValues.put(CurrentWeatherEntry.ICON_NUMBER, weatherObject.getIconNumber());
                contentValues.put(CurrentWeatherEntry.PRESSURE, weatherObject.getPressure());
                contentValues.put(CurrentWeatherEntry.TIME, weatherObject.getTime());
                contentValues.put(CurrentWeatherEntry.TIME_ZONE, weatherObject.getTimeZone());
                contentValues.put(CurrentWeatherEntry.WIND_SPEED, weatherObject.getWindSpeed());
                database.update(CurrentWeatherEntry.TABLE_NAME,
                                    contentValues,
                                    CurrentWeatherEntry._ID+" = ?",
                                    new String[]{Integer.toString(i+1)});
            }
        } catch (Exception e) {
            return false;
        } finally {
            databaseHelper.close();
            if (database != null) {
                database.close();
            }
        }
        return true;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
