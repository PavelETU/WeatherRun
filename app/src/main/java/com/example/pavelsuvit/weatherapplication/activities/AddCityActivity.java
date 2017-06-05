package com.example.pavelsuvit.weatherapplication.activities;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.example.pavelsuvit.weatherapplication.dataPresenters.CitiesIdVirtualDatabase;
import com.example.pavelsuvit.weatherapplication.utils.CityIdCursorWrapper;

public class AddCityActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get search query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SearchTask(this, query);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            //String respond = DatabaseUtils.dumpCursorToString(data);
            CityIdCursorWrapper resultCursor = new CityIdCursorWrapper(data);
            //respond = DatabaseUtils.dumpCursorToString(resultCursor);
            CursorAdapter listAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1, resultCursor,
                    new String[] {CitiesIdVirtualDatabase.CITY_NAME},
                    new int[]{android.R.id.text1}, 0);
            setListAdapter(listAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // Private class for background search
    private static class SearchTask extends AsyncTaskLoader<Cursor> {

        Context context;

        private String searchQuery;

        public SearchTask(Context context, String query) {
            super(context);
            this.context = context;
            searchQuery = query;
        }

        @Override
        public Cursor loadInBackground() {
            CitiesIdVirtualDatabase db = new CitiesIdVirtualDatabase(context);
            return db.searchQuery(searchQuery, new String[]{"rowid", CitiesIdVirtualDatabase.CITY_ID,
                    CitiesIdVirtualDatabase.CITY_NAME,
                    CitiesIdVirtualDatabase.COORD_LAT, CitiesIdVirtualDatabase.COORD_LON});
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
