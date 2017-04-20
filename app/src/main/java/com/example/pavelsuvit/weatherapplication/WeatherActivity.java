package com.example.pavelsuvit.weatherapplication;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<CurrentWeatherData>> {

    //,1735161
    private static final String URL_WEATHER = "http://api.openweathermap.org/data/2.5/group?id=498817,524901,3882428,5128581,1512236,1261481,1609350&APPID=6b8bc04abc1a38d4dbcc15a8f833ff69";
    WeatherRecyclerAdapter adapter;
    List<CurrentWeatherData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        RecyclerView cities = (RecyclerView) findViewById(R.id.cities);
        list = new ArrayList<>();
        //adapter = new WeatherAdapter(this, list);
        adapter = new WeatherRecyclerAdapter(this, list);
        cities.setAdapter(adapter);
        cities.setLayoutManager(new LinearLayoutManager(this));
        //cities.setAdapter(adapter);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ItemTouchHelper mIth = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                list.add(toPos, list.remove(fromPos));
                adapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                fab.show();
                list.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        mIth.attachToRecyclerView(cities);
        getLoaderManager().initLoader(0, null, this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddCityActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<List<CurrentWeatherData>> onCreateLoader(int id, Bundle args) {
        return new WeatherLoader(this, URL_WEATHER);
    }

    @Override
    public void onLoadFinished(Loader<List<CurrentWeatherData>> loader, List<CurrentWeatherData> data) {
        if (data != null) {
            list.addAll(data);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoaderReset(Loader<List<CurrentWeatherData>> loader) {
        list.clear();
        adapter.notifyDataSetChanged();
    }
}
