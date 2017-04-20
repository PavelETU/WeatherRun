package com.example.pavelsuvit.weatherapplication;

import android.text.TextUtils;

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


    public static List<CurrentWeatherData> getCurrentTemp(String url) {
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

    public static List<CurrentWeatherData> extractJSON(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<CurrentWeatherData> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(jsonResponse);
            int n = object.getInt("cnt");
            JSONArray arrayOfCities = object.getJSONArray("list");
            for (int i = 0; i < n; i++) {
                JSONObject cityObject = arrayOfCities.getJSONObject(i);
                String icon = cityObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String cityName = cityObject.getString("name");
                int temp = (int) (cityObject.getJSONObject("main").getDouble("temp") - 273.15);
                list.add(new CurrentWeatherData(cityName, temp, icon));
            }
        } catch (JSONException e) {
            return null;
        }

        return list;
    }

}
