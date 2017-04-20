package com.example.pavelsuvit.weatherapplication;


import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

public class WeatherLoader extends AsyncTaskLoader<List<CurrentWeatherData>> {

    private String url;

    public WeatherLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public List<CurrentWeatherData> loadInBackground() {
        return JSONHelper.getCurrentTemp(url);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
