package com.example.pavelsuvit.weatherapplication.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.pavelsuvit.weatherapplication.R;
import com.example.pavelsuvit.weatherapplication.activities.WeatherActivity;
import com.example.pavelsuvit.weatherapplication.data_presenters.DetailedWeatherData;
import com.example.pavelsuvit.weatherapplication.utils.WeatherDetailedLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailedFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<DetailedWeatherData>>,
        SeekBar.OnSeekBarChangeListener {

    private static final String URL_WEATHER = "http://api.openweathermap.org/data/2.5/forecast?id=";
    private static final String URL_PART_WITH_KEY = "&APPID=6b8bc04abc1a38d4dbcc15a8f833ff69";
    private static String TIME_ZONE;
    private SeekBar weatherPosition;
    private String cityId;
    private List<DetailedWeatherData> weatherDataList;
    private int currentPosition;

    public DetailedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // In case we had data before just load previous data and position for seekbar
        // otherwise setCity from intent if it exist, later new data will be loaded by Loader
        if (savedInstanceState != null) {
            weatherDataList = savedInstanceState.getParcelableArrayList("values");
            currentPosition = savedInstanceState.getInt("currentPosition");
        } else if (getActivity().getIntent().getExtras() != null) {
            String cityIdTmp = getActivity().getIntent().getExtras().getString(WeatherActivity.EXTRA_CITY_ID);
            DetailedWeatherData objectTmp = getActivity().getIntent().getExtras().getParcelable(WeatherActivity.EXTRA_CITY_OBJECT_ID);
            if (cityIdTmp != null && objectTmp != null) {
                setCity(cityIdTmp, objectTmp);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detailed, container, false);
        DetailedWeatherData currentData = weatherDataList.get(currentPosition);
        fillViewWithObject(currentData, view);
        weatherPosition = (SeekBar) view.findViewById(R.id.weatherPosition);
        weatherPosition.setOnSeekBarChangeListener(this);
        weatherPosition.setMax(weatherDataList.size() - 1);
        weatherPosition.setProgress(currentPosition);
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (weatherPosition == null) {
            outState.putInt("currentPosition", currentPosition);
        } else {
            outState.putInt("currentPosition", weatherPosition.getProgress());
        }
        outState.putParcelableArrayList("values", (ArrayList<DetailedWeatherData>) weatherDataList);
    }

    private void fillViewWithObject(DetailedWeatherData currentData, View view) {
        if (view != null) {
            ImageView currentIcon = (ImageView) view.findViewById(R.id.weatherIcon);
            currentIcon.setImageResource(currentData.getIconDrawable());
            TextView weatherText = (TextView) view.findViewById(R.id.chosenWeather);
            weatherText.setText(String.format(Locale.US, "%d\u00b0", currentData.getCurrentWeather()));
            TextView cityText = (TextView) view.findViewById(R.id.cityText);
            cityText.setText(currentData.getCity());
            TextView timeText = (TextView) view.findViewById(R.id.timeText);
            Date dateDisplay = new Date(currentData.getTime() * 1000);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy, h:mm a");
            if (TIME_ZONE != null) {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
            }
            timeText.setText(simpleDateFormat.format(dateDisplay));
            TextView pressureText = (TextView) view.findViewById(R.id.pressureText);
            pressureText.setText(getResources().getString(R.string.pressure, currentData.getPressure()));
            TextView humidityText = (TextView) view.findViewById(R.id.humidityText);
            humidityText.setText(getResources().getString(R.string.humidity, currentData.getHumidity()));
            TextView windText = (TextView) view.findViewById(R.id.windText);
            windText.setText(getResources().getString(R.string.wind, currentData.getWindSpeed()));
        }
    }

    // This function triggers every time when new DetailedFragment is created.
    // In case of fragments this triggers when Fragment created in WeatherActivity.
    // In case of different Activities this triggers by onCreateView.
    public void setCity(String cityId, DetailedWeatherData currentData) {
        this.cityId = cityId;
        weatherDataList = new ArrayList<>();
        // Add the first element to array (from common list fragment),
        weatherDataList.add(currentData);
        TIME_ZONE = currentData.getTimeZone();
    }

    @Override
    public Loader<List<DetailedWeatherData>> onCreateLoader(int id, Bundle args) {
        return new WeatherDetailedLoader(getActivity(), combineURL());
    }

    public String combineURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_WEATHER);
        sb.append(cityId);
        sb.append(URL_PART_WITH_KEY);
        return sb.toString();
    }

    @Override
    public void onLoadFinished(Loader<List<DetailedWeatherData>> loader, List<DetailedWeatherData> data) {
        if (data != null) {
            weatherDataList.addAll(data);
            weatherPosition.setMax(weatherDataList.size() - 1);
        }
        getLoaderManager().destroyLoader(0);
    }


    @Override
    public void onLoaderReset(Loader<List<DetailedWeatherData>> loader) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            fillViewWithObject(weatherDataList.get(progress), getView());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
