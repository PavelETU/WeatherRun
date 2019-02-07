package com.example.pavelsuvit.weatherapplication.data_presenters;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CitiesIdVirtualDatabase {

    private final String TAG = this.getClass().getSimpleName();

    public static final String TABLE_NAME = "CITIES";
    public static final String CITY_ID = "CITY_ID";
    public static final String CITY_NAME = "CITY_NAME";
    public static final String COORD_LON = "LONGITUDE";
    public static final String COORD_LAT = "LATITUDE";

    private static final String DB_NAME = "CityIdDataBase";
    private static final int DB_VERSION = 1;

    private Context mContext;

    private final String DB_PATH;
//            "/data/data/com.example.pavelsuvit.weatherapplication.dataPresenters/databases/";

    public WeatherCityIdDatabaseHelper myOpenHelper;

    public CitiesIdVirtualDatabase(Context context) {
        myOpenHelper = new WeatherCityIdDatabaseHelper(context);
        mContext = context;
        DB_PATH = mContext.getDatabasePath(DB_NAME).getPath();
        try {
            myOpenHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myOpenHelper.openDatabase();
    }

    public Cursor searchQuery(String query, String[] columns) {
        String selection = CITY_NAME + " MATCH ?";
        String[] selectionArgs = new String[]{query + "*"};
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

        private SQLiteDatabase mDatabase;

        public void createDatabase() throws IOException {

            boolean dbExist = checkDatabase();
            if (!dbExist) {
                try {
                    this.getReadableDatabase();

                    copyDatabase();
                } catch (IOException e) {
                    Log.e(TAG, "createDatabase: failed on copying", e);
                }
            }
        }

        private void copyDatabase() throws IOException {

            InputStream mInput = mContext.getAssets().open(DB_NAME);

            OutputStream mOutput = new FileOutputStream(DB_PATH);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = mInput.read(buffer)) > 0) {
                mOutput.write(buffer, 0, length);
            }

            mOutput.flush();
            mOutput.close();
            mInput.close();
        }

        private boolean checkDatabase() {

            SQLiteDatabase checkDB = null;

            try {
                String mPath = DB_PATH;
                checkDB = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READONLY);
            } catch (SQLiteException e) {
                Log.e(TAG, "Database doesn't exist yet");
            }

            if (checkDB != null) {
                checkDB.close();
            }

            return checkDB != null;
        }

        public void openDatabase() {
            mDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        }


        public WeatherCityIdDatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
