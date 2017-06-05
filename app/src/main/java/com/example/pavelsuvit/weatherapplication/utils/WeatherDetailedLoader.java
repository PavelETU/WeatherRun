package com.example.pavelsuvit.weatherapplication.utils;


import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.pavelsuvit.weatherapplication.dataPresenters.DetailedWeatherData;
import com.example.pavelsuvit.weatherapplication.utils.JSONHelper;

import java.util.List;

public class WeatherDetailedLoader extends AsyncTaskLoader<List<DetailedWeatherData>> {

    private String url;

    public WeatherDetailedLoader(Context context, String url) {
        super(context);
        this.url = url;
    }


    @Override
    public List<DetailedWeatherData> loadInBackground() {
        return JSONHelper.getDetailedTemp(url);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
