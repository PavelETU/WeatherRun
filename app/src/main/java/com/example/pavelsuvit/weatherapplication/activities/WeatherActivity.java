package com.example.pavelsuvit.weatherapplication.activities;


import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.pavelsuvit.weatherapplication.R;
import com.example.pavelsuvit.weatherapplication.dataPresenters.DetailedWeatherData;
import com.example.pavelsuvit.weatherapplication.fragments.DetailedFragment;
import com.example.pavelsuvit.weatherapplication.fragments.ListWeatherFragment;
import com.example.pavelsuvit.weatherapplication.utils.LoadCitiesToDatabaseTask;


public class WeatherActivity extends AppCompatActivity
        implements ListWeatherFragment.ItemDeletedFromList,
        ListWeatherFragment.ItemClickedListener,
        ListWeatherFragment.LoaderCondition,
        LoaderManager.LoaderCallbacks<Void> {

    public static final String EXTRA_CITY_ID = "CityId";
    public static final String EXTRA_CITY_OBJECT_ID = "dataObject";

    // Handle the floating button click - call the add city activity
    private FloatingActionButton.OnClickListener floatingBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), MainSearchActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        String newCityExtra = getIntent().getStringExtra(getString(R.string.new_city_extra));
        if (newCityExtra != null) {
            ListWeatherFragment mWeatherFragment =
                    (ListWeatherFragment) getFragmentManager().findFragmentById(R.id.list_fragment_view);
            mWeatherFragment.addCity(newCityExtra);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(floatingBtnListener);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void itemRemovedFromList() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (!fab.isShown()) {
            fab.show();
        }
    }

    @Override
    public void itemFromListClicked(String city_id, DetailedWeatherData dataObject) {
        View detailedFragment = findViewById(R.id.detailed_fragment);
        if (detailedFragment != null) {
            DetailedFragment df = new DetailedFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            df.setCity(city_id, dataObject);
            ft.replace(R.id.detailed_fragment, df);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else {
            Intent intent = new Intent(this, DetailedWeatherActivity.class);
            intent.putExtra(EXTRA_CITY_ID, city_id);
            intent.putExtra(EXTRA_CITY_OBJECT_ID, dataObject);
            startActivity(intent);
        }
    }

    @Override
    public void showRecyclerView() {
        findViewById(R.id.loadProgress).setVisibility(View.GONE);
    }

    @Override
    public void showNoConnectionText() {
        findViewById(R.id.loadProgress).setVisibility(View.GONE);
        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        return new LoadCitiesToDatabaseTask(this);
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }
}
