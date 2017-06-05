package com.example.pavelsuvit.weatherapplication.utils;

import android.text.TextUtils;

import com.example.pavelsuvit.weatherapplication.dataPresenters.DetailedWeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class JSONHelper {
    private JSONHelper() {}
    private static final String TIME_ZONE_API_URL = "https://maps.googleapis.com/maps/api/timezone/json?location=";
    private static final String TIME_ZONE_API_KEY = "&key=AIzaSyDQXd5U-nUeEtoSaoTZNCUl0lVSJksWRXM";

    public static List<DetailedWeatherData> getDetailedTemp(String url) {
        if (url == null) {
            return null;
        }
        URL endURL;
        try {
            endURL = new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
        String jsonResponse = null;

        try {
            jsonResponse = getJSONfromConnection(endURL);
        } catch (IOException e) {
            return null;
        }
        if (jsonResponse == null) {
            return null;
        }

        return extractDetailedJSON(jsonResponse);
    }

    public static List<DetailedWeatherData> getCurrentTemp(String url) {
        if (url == null) {
            return null;
        }
        URL endURL;
        try {
            endURL = new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
        String jsonResponse = null;

        try {
            jsonResponse = getJSONfromConnection(endURL);
        } catch (IOException e) {
            return null;
        }
        if (jsonResponse == null) {
            return null;
        }

        return extractJSON(jsonResponse);
    }

    public static String getTimeZone(double lat, double lon, long timeStamp) {
        String url = TIME_ZONE_API_URL+lat+","+lon+"&timestamp="+timeStamp+TIME_ZONE_API_KEY;
        String result = null;
        URL endURL;
        try {
            endURL = new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
        String jsonResponse = null;
        try {
            jsonResponse = getJSONfromConnection(endURL);
        } catch (IOException e) {
            return null;
        }
        if (jsonResponse == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            result = jsonObject.getString("timeZoneId");
        } catch (JSONException e) {
            return null;
        }
        return result;

    }

    public static String getJSONfromConnection(URL url) throws IOException {
        String jsonResp = "";
        if (url == null) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResp = readFromStream(inputStream);
            } else {
                return null;
            }

        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResp;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        return sb.toString();
    }

    public static List<DetailedWeatherData> extractJSON(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<DetailedWeatherData> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(jsonResponse);
            int n = object.getInt("cnt");
            JSONArray arrayOfCities = object.getJSONArray("list");
            for (int i = 0; i < n; i++) {
                JSONObject cityObject = arrayOfCities.getJSONObject(i);
                double lon = cityObject.getJSONObject("coord").getDouble("lon");
                double lat = cityObject.getJSONObject("coord").getDouble("lat");
                String icon = cityObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String cityName = cityObject.getString("name");
                int temp = (int) (cityObject.getJSONObject("main").getDouble("temp") - 273.15);
                int pressure = (int) cityObject.getJSONObject("main").getDouble("pressure");
                int humidity = cityObject.getJSONObject("main").getInt("humidity");
                int wind = cityObject.getJSONObject("wind").getInt("speed");
                long time = cityObject.getLong("dt");
                String timeZone = getTimeZone(lat, lon, time);
                list.add(new DetailedWeatherData(cityName, temp, icon, time, timeZone, pressure, humidity, wind));
            }
        } catch (JSONException e) {
            return null;
        }

        return list;
    }

    public static List<DetailedWeatherData> extractDetailedJSON(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<DetailedWeatherData> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(jsonResponse);
            String cityName = object.getJSONObject("city").getString("name");
            int n = object.getInt("cnt");
            JSONArray arrayOfCities = object.getJSONArray("list");
            for (int i = 0; i < n; i++) {
                JSONObject detailedWeatherObject = arrayOfCities.getJSONObject(i);
                long time = detailedWeatherObject.getLong("dt");
                JSONObject mainObject = detailedWeatherObject.getJSONObject("main");
                int temp = (int) (mainObject.getDouble("temp") - 273.15);
                int pressure = (int) mainObject.getDouble("pressure");
                int humidity = mainObject.getInt("humidity");
                String icon = detailedWeatherObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                int windSpeed = detailedWeatherObject.getJSONObject("wind").getInt("speed");
                list.add(new DetailedWeatherData(cityName, temp, icon, time, pressure, humidity, windSpeed));
            }
        } catch (JSONException e) {
            return null;
        }

        return list;
    }

}
