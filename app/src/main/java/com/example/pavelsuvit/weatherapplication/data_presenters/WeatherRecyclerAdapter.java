package com.example.pavelsuvit.weatherapplication.data_presenters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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
        final DetailedWeatherData weatherData = weatherList.get(position);
        holder.cityText.setText(weatherData.getCity());
        holder.currentWeather.setText(String.format(Locale.US, "%d\u00b0", weatherData.getCurrentWeather()));
        holder.weatherIcon.setImageResource(weatherData.getIconDrawable());
        Date dateDisplay = new Date(weatherData.getTime()*1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        if (weatherData.getTimeZone() != null) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(weatherData.getTimeZone()));
        }
        holder.weatherDate.setText(simpleDateFormat.format(dateDisplay));
        simpleDateFormat.applyPattern("h:mm a");
        holder.weatherTime.setText(simpleDateFormat.format(dateDisplay));
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    private static WeatherListListener listener;

    public interface WeatherListListener {
        void itemClicked(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView cityText;
        public TextView currentWeather;
        public ImageView weatherIcon;
        public TextView weatherDate;
        public TextView weatherTime;
        public View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            cityText = (TextView) itemView.findViewById(R.id.cityText);
            currentWeather = (TextView) itemView.findViewById(R.id.currentWeather);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weatherIcon);
            weatherDate = (TextView) itemView.findViewById(R.id.weatherDate);
            weatherTime = (TextView) itemView.findViewById(R.id.weatherTime);
            mView = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.itemClicked(v, getLayoutPosition());
        }
    }
    private List<DetailedWeatherData> weatherList;
    private Context context;

    public WeatherRecyclerAdapter(Context context, List<DetailedWeatherData> list, WeatherListListener listener) {
        this.weatherList = list;
        this.context = context;
        this.listener = listener;
    }
    private Context getContext() {
        return context;
    }


}
