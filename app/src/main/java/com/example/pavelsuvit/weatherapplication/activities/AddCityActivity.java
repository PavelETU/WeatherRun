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
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.pavelsuvit.weatherapplication.R;
import com.example.pavelsuvit.weatherapplication.data_presenters.CitiesIdVirtualDatabase;
import com.example.pavelsuvit.weatherapplication.utils.CityIdCursorWrapper;

public class AddCityActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
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
        return new SearchTask(getApplicationContext(), query);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            CityIdCursorWrapper resultCursor = new CityIdCursorWrapper(data);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor mCursor =
                ((CursorWrapper)getListView().getItemAtPosition(position)).getWrappedCursor();
        mCursor.moveToFirst();
        String cityId = mCursor.getString(mCursor.getColumnIndex(CitiesIdVirtualDatabase.CITY_ID));
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(getString(R.string.new_city_extra), cityId);
        startActivity(intent);
    }

    // Private class for background search
    private static class SearchTask extends AsyncTaskLoader<Cursor> {

        private CitiesIdVirtualDatabase db;
        private String searchQuery;

        SearchTask(Context context, String query) {
            super(context);
            db = new CitiesIdVirtualDatabase(context);
            searchQuery = query;
        }

        @Override
        public Cursor loadInBackground() {
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
