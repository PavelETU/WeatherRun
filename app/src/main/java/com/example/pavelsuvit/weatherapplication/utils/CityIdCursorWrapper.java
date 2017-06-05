package com.example.pavelsuvit.weatherapplication.utils;


import android.database.Cursor;
import android.database.CursorWrapper;

public class CityIdCursorWrapper extends CursorWrapper {

    public CityIdCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    // Change "rowid" of Virtual Table to _id for CursorAdapter
    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        if (columnName.equalsIgnoreCase("_id")) {
            columnName = "rowid";
        }
        return super.getColumnIndexOrThrow(columnName);
    }
}
