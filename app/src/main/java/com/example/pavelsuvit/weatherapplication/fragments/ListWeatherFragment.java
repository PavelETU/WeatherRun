package com.example.pavelsuvit.weatherapplication.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pavelsuvit.weatherapplication.R;
import com.example.pavelsuvit.weatherapplication.dataPresenters.DetailedWeatherData;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabase;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabaseContract.CurrentWeatherEntry;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherRecyclerAdapter;
import com.example.pavelsuvit.weatherapplication.utils.LoadDataFromDatabaseTask;
import com.example.pavelsuvit.weatherapplication.utils.SaveDataToDatabaseTask;
import com.example.pavelsuvit.weatherapplication.utils.WeatherLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListWeatherFragment extends Fragment
        implements WeatherRecyclerAdapter.WeatherListListener {

    // List of pre installed cities - they will make a list with current weather
    private List<String> citiesID;
    private static final int LOAD_FROM_INTERNET_LOADER_ID = 1;
    private static final int SAVE_TO_DB_LOADER_ID = 2;
    private static final int LOAD_FROM_DB_LOADER_ID = 3;
    private static final int SAVE_TO_DB_DURING_PAUSE_LOADER_ID = 4;
    private static final String URL_WEATHER = "http://api.openweathermap.org/data/2.5/group?id=";
    private static final String URL_PART_WITH_KEY = "&APPID=6b8bc04abc1a38d4dbcc15a8f833ff69";
    private WeatherRecyclerAdapter adapter;
    private List<DetailedWeatherData> currentWeatherList;
    private SwipeRefreshLayout refreshLayout;
    private LoaderManager.LoaderCallbacks<Boolean> loaderToDB =
            new LoaderManager.LoaderCallbacks<Boolean>() {
                @Override
                public Loader<Boolean> onCreateLoader(int id, Bundle args) {
                    if (id == SAVE_TO_DB_LOADER_ID) {
                        // Last parameter is Update = true (we already have cityId column).
                        return new SaveDataToDatabaseTask(getActivity(), currentWeatherList, citiesID, true);
                    } else {
                        // Last parameter is Update = false (rewrite whole database).
                        return new SaveDataToDatabaseTask(getActivity(), currentWeatherList, citiesID, false);
                    }
                }

                @Override
                public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
                    if (!data) {
                        Toast.makeText(getActivity(), "Cannot save data to database. For proper offline mode reinstall app.", Toast.LENGTH_SHORT).show();
                    }
                    getLoaderManager().destroyLoader(SAVE_TO_DB_LOADER_ID);
                }

                @Override
                public void onLoaderReset(Loader<Boolean> loader) {

                }
            };

    private LoaderManager.LoaderCallbacks<List<DetailedWeatherData>> loaderFromInternet =
            new LoaderManager.LoaderCallbacks<List<DetailedWeatherData>>() {
                @Override
                public Loader<List<DetailedWeatherData>> onCreateLoader(int id, Bundle args) {
                    if (id == LOAD_FROM_INTERNET_LOADER_ID) {
                        return new WeatherLoader(getActivity(), combineURL());
                    } else {
                        return new LoadDataFromDatabaseTask(getActivity());
                    }
                }

                @Override
                public void onLoadFinished(Loader<List<DetailedWeatherData>> loader, List<DetailedWeatherData> data) {
                    currentWeatherList.clear();
                    if (data != null && data.size() != 0 && data.get(0).getCity() != null) {
                        loaderCondition.showRecyclerView();
                        currentWeatherList.addAll(data);
                        adapter.notifyDataSetChanged();
                        // Save Current Data to database if it's load from internet
                        if (loader.getId() == LOAD_FROM_INTERNET_LOADER_ID) {
                            getLoaderManager().initLoader(SAVE_TO_DB_LOADER_ID, null, loaderToDB);
                        }
                    } else if (data != null && data.size() == 0) {
                        loaderCondition.showRecyclerView();
                    } else {
                        loaderCondition.showNoConnectionText();
                    }
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                    getLoaderManager().destroyLoader(loader.getId());
                }

                @Override
                public void onLoaderReset(Loader<List<DetailedWeatherData>> loader) {
                    //adapter.clear();
                    currentWeatherList.clear();
                    adapter.notifyDataSetChanged();
                }
            };

    public void addCity(String cityId) {
        citiesID.add(cityId);
        launchCityLoader();
    }

    @Override
    public void itemClicked(View view, int position) {
        if (eventItemClicked != null) {
            eventItemClicked.itemFromListClicked(citiesID.get(position), currentWeatherList.get(position));
        }
    }

    // Create interface for handling fab visibility (in WeatherActivity) after deleting element from list
    public interface ItemDeletedFromList {
        void itemRemovedFromList();
    }

    private ItemDeletedFromList eventItemDeleted;

    // Through this interface call to detailedWeather happens(in WeatherActivity)
    public interface ItemClickedListener {
        void itemFromListClicked(String cityID, DetailedWeatherData object);
    }

    private ItemClickedListener eventItemClicked;

    // Through this interface hiding of ProgressBar happens
    public interface LoaderCondition {
        void showRecyclerView();

        void showNoConnectionText();
    }

    private LoaderCondition loaderCondition;

    public ListWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.eventItemDeleted = (ItemDeletedFromList) activity;
        this.eventItemClicked = (ItemClickedListener) activity;
        this.loaderCondition = (LoaderCondition) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_list_weather, container, false);
        final RecyclerView cities = (RecyclerView) layout.findViewById(R.id.cities);
        //TextView emptyTV = (TextView)layout.findViewById(R.id.emptyView);
        //emptyTV.setVisibility(View.VISIBLE);
        //cities.setVisibility(View.GONE);
        currentWeatherList = new ArrayList<>();
        if (savedInstanceState != null) {
            currentWeatherList = savedInstanceState.getParcelableArrayList("values");
            citiesID = savedInstanceState.getStringArrayList("citiesID");
            //adapter.notifyDataSetChanged();
        } else {
            citiesID = loadCitiesIDFromDatabase(getActivity());
        }
        adapter = new WeatherRecyclerAdapter(getActivity(), currentWeatherList, this);
        cities.setAdapter(adapter);
        cities.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper mIth = new ItemTouchHelper
                (new ItemTouchHelper.SimpleCallback
                        (ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        moveRowsInTable(fromPos, toPos);
                        citiesID.add(toPos, citiesID.remove(fromPos));
                        currentWeatherList.add(toPos, currentWeatherList.remove(fromPos));
                        adapter.notifyItemMoved(fromPos, toPos);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        removeRowFromDB(position);
                        currentWeatherList.remove(position);
                        citiesID.remove(position);
                        adapter.notifyItemRemoved(position);
                        eventItemDeleted.itemRemovedFromList();
                    }
                });
        mIth.attachToRecyclerView(cities);
        refreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startRefresh();
            }
        });
        return layout;
    }

    // Call loader launching in onActivityCreated,
    // otherwise app will crash inasmuch as it sets view in activity
    // (through loaderCondition interface)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        launchCityLoader();
    }

    // TODO move moveRowsInTable to non UI thread and set try catch
    private void moveRowsInTable(int fromPosition, int toPosition) {
        DetailedWeatherData fromObject = currentWeatherList.get(fromPosition),
                toObject = currentWeatherList.get(toPosition);
        WeatherDatabase dbHelper = new WeatherDatabase(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(CurrentWeatherEntry.TABLE_NAME,
                new String[]{CurrentWeatherEntry._ID},
                CurrentWeatherEntry.CITY_ID + " = ? ",
                new String[]{citiesID.get(fromPosition)}, null, null, null);
        int indexId = cursor.getColumnIndex(CurrentWeatherEntry._ID);
        int fromId = 0, toId = 0;
        if (cursor.moveToNext()) {
            fromId = cursor.getInt(indexId);
        }
        cursor = db.query(CurrentWeatherEntry.TABLE_NAME,
                new String[]{CurrentWeatherEntry._ID},
                CurrentWeatherEntry.CITY_ID + " = ?",
                new String[]{citiesID.get(toPosition)}, null, null, null);
        if (cursor.moveToNext()) {
            toId = cursor.getInt(indexId);
        }
        updateThisRowWithThisObject(db, fromPosition, fromObject, toId);
        updateThisRowWithThisObject(db, toPosition, toObject, fromId);
        cursor.close();
        dbHelper.close();
        db.close();
    }

    private void updateThisRowWithThisObject(SQLiteDatabase db, int position, DetailedWeatherData object, int toPosition) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CurrentWeatherEntry.CITY_ID, citiesID.get(position));
        contentValues.put(CurrentWeatherEntry.CITY_NAME, object.getCity());
        contentValues.put(CurrentWeatherEntry.CURRENT_WEATHER, object.getCurrentWeather());
        contentValues.put(CurrentWeatherEntry.TIME, object.getTime());
        contentValues.put(CurrentWeatherEntry.TIME_ZONE, object.getTimeZone());
        contentValues.put(CurrentWeatherEntry.HUMIDITY, object.getHumidity());
        contentValues.put(CurrentWeatherEntry.WIND_SPEED, object.getWindSpeed());
        contentValues.put(CurrentWeatherEntry.PRESSURE, object.getPressure());
        contentValues.put(CurrentWeatherEntry.ICON_NUMBER, object.getIconNumber());
        db.update(CurrentWeatherEntry.TABLE_NAME, contentValues,
                CurrentWeatherEntry._ID + " = ?",
                new String[]{Integer.toString(toPosition)});
    }

    // TODO move removeRowFromDB to non UI thread and set try catch
    private void removeRowFromDB(int position) {
        WeatherDatabase dbHelper = new WeatherDatabase(getActivity());
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(CurrentWeatherEntry.TABLE_NAME, CurrentWeatherEntry.CITY_ID + " = ?",
                    new String[]{citiesID.get(position)});
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Problems with data persistence.", Toast.LENGTH_SHORT).show();
        }
        dbHelper.close();
        if (db != null) {
            db.close();
        }
    }

    // TODO move loadCitiesIDFromDatabase to non UI thread and set try catch
    private List<String> loadCitiesIDFromDatabase(Context context) {
        WeatherDatabase dbHelper = new WeatherDatabase(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CurrentWeatherEntry.TABLE_NAME,
                new String[]{CurrentWeatherEntry.CITY_ID}, null, null, null, null, null);
        int cityIdIndex = cursor.getColumnIndex(CurrentWeatherEntry.CITY_ID);
        List<String> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(cursor.getString(cityIdIndex));
        }
        cursor.close();
        db.close();
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("values", (ArrayList<DetailedWeatherData>) currentWeatherList);
        outState.putStringArrayList("citiesID", (ArrayList<String>) citiesID);
    }

    // onPause save database. We will rewrite the whole table,
    // cause user could change order and amount of cities dramatically
    @Override
    public void onPause() {
        super.onPause();
        //getLoaderManager().initLoader(SAVE_TO_DB_DURING_PAUSE_LOADER_ID, null, loaderToDB);
    }

    public void startRefresh() {
        launchCityLoader();
    }

    private void launchCityLoader() {
        // If we don't have any cities to load - just return without loader launch
        if (citiesID == null || citiesID.size() == 0) {
            loaderCondition.showRecyclerView();
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Add city to display.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        // Check network connection - if connect established - load from internet, otherwise from database
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            getLoaderManager().initLoader(LOAD_FROM_INTERNET_LOADER_ID, null, loaderFromInternet);
        } else {
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            } else {
                getLoaderManager().initLoader(LOAD_FROM_DB_LOADER_ID, null, loaderFromInternet);
            }
        }
    }

    public String combineURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_WEATHER);
        for (int i = 0; i < citiesID.size() - 1; i++) {
            sb.append(citiesID.get(i)).append(",");
        }
        sb.append(citiesID.get(citiesID.size() - 1));
        sb.append(URL_PART_WITH_KEY);
        return sb.toString();
    }


}
