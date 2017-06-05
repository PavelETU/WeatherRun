package com.example.pavelsuvit.weatherapplication.dataPresenters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabaseContract.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherDatabase extends SQLiteOpenHelper {
    // We have two tables in database for keeping weather.
    // In forecast table we keep track corresponding row in currentWeather table
    // and in currentWeather table we keep cityId - we will make http request based on those values.
    private static final String DB_NAME = "WeatherDatabase";
    private static final int DB_VERSION = 1;

    public WeatherDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String currentWeatherCreateStatement = "CREATE TABLE " + CurrentWeatherEntry.TABLE_NAME
                + " ("+CurrentWeatherEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CurrentWeatherEntry.CITY_ID + " TEXT, " + CurrentWeatherEntry.CITY_NAME + " TEXT, "
                + CurrentWeatherEntry.CURRENT_WEATHER + " INTEGER, " + CurrentWeatherEntry.HUMIDITY + " INTEGER, "
                + CurrentWeatherEntry.PRESSURE + " INTEGER, " + CurrentWeatherEntry.WIND_SPEED + " INTEGER, "
                + CurrentWeatherEntry.TIME + " INTEGER, " + CurrentWeatherEntry.TIME_ZONE + " TEXT, "
                + CurrentWeatherEntry.ICON_NUMBER + " TEXT);";
        final String forecastCreateStatement = "CREATE TABLE " + ForecastEntry.TABLE_NAME
                + " ("+ForecastEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ForecastEntry.CORRESPONDING_ROW_ID_IN_CURRENT_WEATHER_TABLE + " TEXT, "
                + ForecastEntry.CITY_NAME + " TEXT, "
                + ForecastEntry.CURRENT_WEATHER + " INTEGER, " + ForecastEntry.HUMIDITY + " INTEGER, "
                + ForecastEntry.PRESSURE + " INTEGER, " + ForecastEntry.WIND_SPEED + " INTEGER, "
                + ForecastEntry.TIME + " INTEGER, " + ForecastEntry.TIME_ZONE + " TEXT, "
                + ForecastEntry.ICON_NUMBER + " TEXT);";
        db.execSQL(currentWeatherCreateStatement);
        db.execSQL(forecastCreateStatement);
        insertDefault(db);
    }

    public void insertDefault(SQLiteDatabase db) {
        List<String> citiesID = new ArrayList<>(Arrays.asList(new String[] {"498817","561887","1735161",
                "3067696","456172","524901","1512236","1261481","1609350", "4164143", "3081368"}));
        ContentValues contentValues = new ContentValues();
        for (String cityId : citiesID) {
            contentValues.put(CurrentWeatherEntry.CITY_ID, cityId);
            db.insert(CurrentWeatherEntry.TABLE_NAME, null, contentValues);
            contentValues.clear();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
