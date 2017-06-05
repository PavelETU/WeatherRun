package com.example.pavelsuvit.weatherapplication.dataPresenters;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class CitiesIdVirtualDatabase {
    public static final String TABLE_NAME = "CITIES";
    public static final String CITY_ID = "CITY_ID";
    public static final String CITY_NAME = "CITY_NAME";
    public static final String COORD_LON = "LONGITUDE";
    public static final String COORD_LAT = "LATITUDE";

    private static final String DB_NAME = "CityIdDataBase";
    private static final int DB_VERSION = 1;

    public SQLiteOpenHelper myOpenHelper;

    public CitiesIdVirtualDatabase(Context context) {
        myOpenHelper = new WeatherCityIdDatabaseHelper(context);
    }

    public Cursor searchQuery(String query, String[] columns) {
        String selection = CITY_NAME + " MATCH ?";
        String[] selectionArgs = new String[] {query + "*"};
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);
        Cursor cursor = builder.query(myOpenHelper.getReadableDatabase(), columns,
                selection, selectionArgs, null, null, null);
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private class WeatherCityIdDatabaseHelper extends SQLiteOpenHelper {

        private static final String SQL_CREATE_ENTRIES =
                "CREATE VIRTUAL TABLE " + TABLE_NAME +
                        " USING fts3 (" + CITY_ID + "," + CITY_NAME + ", " +
                        COORD_LON + ", " + COORD_LAT + ")";


        public WeatherCityIdDatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
