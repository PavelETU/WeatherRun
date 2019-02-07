package com.example.pavelsuvit.weatherapplication.utils;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pavelsuvit.weatherapplication.data_presenters.DetailedWeatherData;
import com.example.pavelsuvit.weatherapplication.data_presenters.WeatherDatabase;
import com.example.pavelsuvit.weatherapplication.data_presenters.WeatherDatabaseContract.CurrentWeatherEntry;

import java.util.List;

/**
 * This AsyncTaskLoader will save weather data to the database
 */

public class SaveDataToDatabaseTask extends AsyncTaskLoader<Boolean> {

    private List<DetailedWeatherData> weatherDataList;
    private SQLiteOpenHelper databaseHelper;
    private List<String> citiesID;
    private boolean update;

    public SaveDataToDatabaseTask(Context context, List<DetailedWeatherData> weatherDataList, List<String> citiesID, boolean update) {
        super(context);
        this.weatherDataList = weatherDataList;
        databaseHelper = new WeatherDatabase(context);
        this.citiesID = citiesID;
        this.update = update;
    }

    @Override
    public Boolean loadInBackground() {
        SQLiteDatabase database = null;
        try {
            database = databaseHelper.getWritableDatabase();
            if (update) {
                databaseUpdate(database);
            } else {
                rewriteDatabase(database);
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

    private void databaseUpdate(SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < weatherDataList.size(); i++) {
            DetailedWeatherData weatherObject = weatherDataList.get(i);
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
                    CurrentWeatherEntry.CITY_ID+" = ?",
                    new String[]{citiesID.get(i)});
            contentValues.clear();
        }
    }

    private void rewriteDatabase(SQLiteDatabase database) {
        database.delete(CurrentWeatherEntry.TABLE_NAME, null, null);
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < weatherDataList.size(); i++) {
            DetailedWeatherData weatherObject = weatherDataList.get(i);
            contentValues.put(CurrentWeatherEntry.CITY_ID, citiesID.get(i));
            contentValues.put(CurrentWeatherEntry.CITY_NAME, weatherObject.getCity());
            contentValues.put(CurrentWeatherEntry.CURRENT_WEATHER, weatherObject.getCurrentWeather());
            contentValues.put(CurrentWeatherEntry.HUMIDITY, weatherObject.getHumidity());
            contentValues.put(CurrentWeatherEntry.ICON_NUMBER, weatherObject.getIconNumber());
            contentValues.put(CurrentWeatherEntry.PRESSURE, weatherObject.getPressure());
            contentValues.put(CurrentWeatherEntry.TIME, weatherObject.getTime());
            contentValues.put(CurrentWeatherEntry.TIME_ZONE, weatherObject.getTimeZone());
            contentValues.put(CurrentWeatherEntry.WIND_SPEED, weatherObject.getWindSpeed());
            database.insert(CurrentWeatherEntry.TABLE_NAME, null, contentValues);
            contentValues.clear();
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
