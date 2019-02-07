package com.example.pavelsuvit.weatherapplication.data_presenters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pavelsuvit.weatherapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.ViewHolder> {
    private List<DetailedWeatherData> weatherList;
    private WeatherListListener listener;

    public WeatherRecyclerAdapter(List<DetailedWeatherData> list, WeatherListListener listener) {
        this.weatherList = list;
        this.listener = listener;
    }

    @Override
    @NonNull
    public WeatherRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View endView = inflater.inflate(R.layout.row_with_city, parent, false);
        return new ViewHolder(endView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final DetailedWeatherData weatherData = weatherList.get(position);
        holder.cityText.setText(weatherData.getCity());
        holder.currentWeather.setText(String.format(Locale.US, "%d\u00b0", weatherData.getCurrentWeather()));
        holder.weatherIcon.setImageResource(weatherData.getIconDrawable());
        Date dateDisplay = new Date(weatherData.getTime() * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        if (weatherData.getTimeZone() != null) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(weatherData.getTimeZone()));
        }
        holder.weatherDate.setText(simpleDateFormat.format(dateDisplay));
        simpleDateFormat.applyPattern("h:mm a");
        holder.weatherTime.setText(simpleDateFormat.format(dateDisplay));
        holder.itemView.setOnClickListener(v -> listener.itemClicked(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityText;
        TextView currentWeather;
        ImageView weatherIcon;
        TextView weatherDate;
        TextView weatherTime;
        View mView;

        ViewHolder(View itemView) {
            super(itemView);
            cityText = itemView.findViewById(R.id.cityText);
            currentWeather = itemView.findViewById(R.id.currentWeather);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            weatherDate = itemView.findViewById(R.id.weatherDate);
            weatherTime = itemView.findViewById(R.id.weatherTime);
            mView = itemView;
        }
    }

    public interface WeatherListListener {
        void itemClicked(int position);
    }

}
