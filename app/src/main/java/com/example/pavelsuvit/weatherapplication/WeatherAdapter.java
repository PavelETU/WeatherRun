package com.example.pavelsuvit.weatherapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends ArrayAdapter<CurrentWeatherData> {
    public WeatherAdapter(Context context, List<CurrentWeatherData> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result = convertView;
        CurrentWeatherData currentWeatherData = getItem(position);
        if (result == null) {
            result = LayoutInflater.from(getContext()).inflate(R.layout.row_with_city, parent, false);
        }
        TextView cityName = (TextView) result.findViewById(R.id.cityText);
        cityName.setText(currentWeatherData.getCity());
        TextView currentWeather = (TextView) result.findViewById(R.id.currentWeather);
        currentWeather.setText(String.format(Locale.US, "%d", currentWeatherData.getCurrentWeather()) );

        ImageView weatherIcon = (ImageView) result.findViewById(R.id.weatherIcon);
        weatherIcon.setImageResource(getIconDrawable(currentWeatherData.getIconNumber()));

        return result;
    }
    private int getIconDrawable(String iconString) {
        int result;
        switch (iconString) {
            case "01d":
                result = R.drawable.w01d;
                break;
            case "02d":
                result = R.drawable.w02d;
                break;
            case "03d":
            case "03n":
                result = R.drawable.w03;
                break;
            case "04d":
            case "04n":
                result = R.drawable.w04;
                break;
            case "09d":
            case "09n":
                result = R.drawable.w09;
                break;
            case "10d":
                result = R.drawable.w10d;
                break;
            case "10n":
                result = R.drawable.w10n;
                break;
            case "11d":
            case "11n":
                result = R.drawable.w11;
                break;
            case "13d":
            case "13n":
                result = R.drawable.w13;
                break;
            case "50d":
            case "50n":
                result = R.drawable.w50;
                break;
            default:
                result = R.drawable.w01d;
                break;
        }
        return result;
    }
}
