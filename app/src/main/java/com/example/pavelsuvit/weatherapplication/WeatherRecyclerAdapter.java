package com.example.pavelsuvit.weatherapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;


public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.ViewHolder> {

    @Override
    public WeatherRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View endView = inflater.inflate(R.layout.row_with_city, parent, false);
        ViewHolder viewHolder = new ViewHolder(endView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CurrentWeatherData weatherData = weatherList.get(position);
        holder.cityText.setText(weatherData.getCity());
        holder.currentWeather.setText(String.format(Locale.US, "%d", weatherData.getCurrentWeather()));
        holder.weatherIcon.setImageResource(getIconDrawable(weatherData.getIconNumber()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedInformation.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cityText;
        public TextView currentWeather;
        public ImageView weatherIcon;
        public View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            cityText = (TextView) itemView.findViewById(R.id.cityText);
            currentWeather = (TextView) itemView.findViewById(R.id.currentWeather);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weatherIcon);
            mView = itemView;
        }
    }
    private List<CurrentWeatherData> weatherList;
    private Context context;

    public WeatherRecyclerAdapter(Context context, List<CurrentWeatherData> list) {
        this.weatherList = list;
        this.context = context;
    }
    private Context getContext() {
        return context;
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
