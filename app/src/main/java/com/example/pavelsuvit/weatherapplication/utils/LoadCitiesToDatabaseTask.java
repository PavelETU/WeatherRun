package com.example.pavelsuvit.weatherapplication.utils;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.pavelsuvit.weatherapplication.data_presenters.CitiesIdVirtualDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadCitiesToDatabaseTask extends AsyncTaskLoader<Void> {

    private CitiesIdVirtualDatabase db;
    private AssetManager assets;

    public LoadCitiesToDatabaseTask(Context context) {
        super(context);
        db = new CitiesIdVirtualDatabase(context);
        assets = context.getAssets();
    }

    @Override
    public Void loadInBackground() {
        // If database doesn't exist yet
//        SQLiteOpenHelper dbHelper = new CitiesIdVirtualDatabase(context).myOpenHelper;
//        SQLiteDatabase myDB= dbHelper.getWritableDatabase();
//        Cursor myCursor = myDB.rawQuery("SELECT COUNT(*) FROM " + CitiesIdVirtualDatabase.TABLE_NAME, null);
//        myCursor.moveToFirst();
//        int countRows = myCursor.getInt(0);
//        if (countRows > 0) {
//            return null;
//        }
//        readCitiesfromFile(myDB);
//        if (result.length() != 0) {
//            Log.i("LoadCities", "loadInBackground: result");
//        }
        /*try {
            JSONArray citiesJSONArray = new JSONArray(jsonForDatabase);
            for (int i = 0; i < citiesJSONArray.length(); i++) {
                ContentValues values = new ContentValues();
                String currentId = citiesJSONArray.getJSONObject(i).getString("id");
                String currentCityName = citiesJSONArray.getJSONObject(i).getString("name");
                String currentCountryCode = citiesJSONArray.getJSONObject(i).getString("country");
                String currentLongitude = citiesJSONArray.getJSONObject(i).getJSONObject("coord").getString("lon");
                String currentLatitude = citiesJSONArray.getJSONObject(i).getJSONObject("coord").getString("lat");
                values.put(CitiesIdVirtualDatabase.CITY_ID, currentId);
                values.put(CitiesIdVirtualDatabase.CITY_NAME, currentCityName+", "+currentCountryCode);
                values.put(CitiesIdVirtualDatabase.COORD_LON, currentLongitude);
                values.put(CitiesIdVirtualDatabase.COORD_LAT, currentLatitude);
                myDB.insert(CitiesIdVirtualDatabase.TABLE_NAME, null, values);
            }
        } catch (JSONException e) {
            Log.e("LoadCitiesToDBTask", e.getMessage());
        }*/
//        myCursor.close();
//        myDB.close();
        return null;
    }

    private void readCitiesfromFile(SQLiteDatabase myDB) {
        StringBuilder result = new StringBuilder();
        InputStream fileStream = null;
        try {
            fileStream = assets.open("city_list.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, "UTF-8"), 8);
            String line = null;
            myDB.beginTransaction();
            ContentValues values = new ContentValues();
            while ((line = reader.readLine()) != null) {
                String [] cityStrings = line.split("\t");
                if (!cityStrings[0].equals("id")) {
//                    result.append(line);
                    values.put(CitiesIdVirtualDatabase.CITY_ID, cityStrings[0]);
                    values.put(CitiesIdVirtualDatabase.CITY_NAME, cityStrings[1] + ", " + cityStrings[4]);
                    values.put(CitiesIdVirtualDatabase.COORD_LON, cityStrings[3]);
                    values.put(CitiesIdVirtualDatabase.COORD_LAT, cityStrings[2]);
                    myDB.insert(CitiesIdVirtualDatabase.TABLE_NAME, null, values);
                }
            }
            myDB.setTransactionSuccessful();
        } catch (IOException ex) {
            return;
        } finally {
            myDB.endTransaction();
            Log.i("Loading", "Done with database");
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    Log.e("Main", e.getMessage());
                }
            }
        }

//        return result.toString();
    }
}
