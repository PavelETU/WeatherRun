package com.example.pavelsuvit.weatherapplication.fragments;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pavelsuvit.weatherapplication.dataPresenters.DetailedWeatherData;
import com.example.pavelsuvit.weatherapplication.R;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabase;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherDatabaseContract.CurrentWeatherEntry;
import com.example.pavelsuvit.weatherapplication.utils.LoadDataFromDatabaseTask;
import com.example.pavelsuvit.weatherapplication.utils.SaveDataToDatabaseTask;
import com.example.pavelsuvit.weatherapplication.utils.WeatherLoader;
import com.example.pavelsuvit.weatherapplication.dataPresenters.WeatherRecyclerAdapter;

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
    private static final String URL_WEATHER = "http://api.openweathermap.org/data/2.5/group?id=";
    private static final String URL_PART_WITH_KEY = "&APPID=6b8bc04abc1a38d4dbcc15a8f833ff69";
    private WeatherRecyclerAdapter adapter;
    private List<DetailedWeatherData> currentWeatherList;
    private SwipeRefreshLayout refreshLayout;
    private LoaderManager.LoaderCallbacks<Boolean> loaderToDB =
            new LoaderManager.LoaderCallbacks<Boolean>() {
                @Override
                public Loader<Boolean> onCreateLoader(int id, Bundle args) {
                    return new SaveDataToDatabaseTask(getActivity(), currentWeatherList);
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
            new LoaderManager.LoaderCallbacks<List<DetailedWeatherData>> () {
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
                    if (data != null && data.get(0).getCity() != null) {
                        loaderCondition.showRecyclerView();
                        currentWeatherList.addAll(data);
                        adapter.notifyDataSetChanged();
                        // Save Current Data to database if it's load from internet
                        if (loader.getId() == LOAD_FROM_INTERNET_LOADER_ID) {
                            getLoaderManager().initLoader(SAVE_TO_DB_LOADER_ID, null, loaderToDB);
                        }
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
        // Check network connection - if connect established - load from internet, otherwise from database
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            getLoaderManager().initLoader(LOAD_FROM_INTERNET_LOADER_ID, null, loaderFromInternet);
        } else {
            getLoaderManager().initLoader(LOAD_FROM_DB_LOADER_ID, null, loaderFromInternet);
        }
        return layout;
    }
    // TODO move moveRowsInTable to non UI thread and set try catch
    private void moveRowsInTable(int fromPosition, int toPosition) {
        DetailedWeatherData fromObject = currentWeatherList.get(fromPosition),
                            toObject = currentWeatherList.get(fromPosition);
        WeatherDatabase dbHelper = new WeatherDatabase(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(CurrentWeatherEntry.CITY_ID, );
        //db.update(CurrentWeatherEntry.TABLE_NAME, );
        dbHelper.close();
        db.close();
    }

    // TODO move removeRowFromDB to non UI thread and set try catch
    private void removeRowFromDB(int position) {
        WeatherDatabase dbHelper = new WeatherDatabase(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(CurrentWeatherEntry.TABLE_NAME, CurrentWeatherEntry._ID+" = ?",
                                    new String[] {Integer.toString(position+1)});
        dbHelper.close();
        db.close();
    }

    // TODO move loadCitiesIDFromDatabase to non UI thread and set try catch
    private List<String> loadCitiesIDFromDatabase(Context context) {
        WeatherDatabase dbHelper = new WeatherDatabase(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CurrentWeatherEntry.TABLE_NAME,
                new String[] {CurrentWeatherEntry.CITY_ID}, null, null, null, null, null);
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

    public void startRefresh() {
        // Check network connection - if connect established - load from internet, otherwise from database
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            getLoaderManager().initLoader(LOAD_FROM_INTERNET_LOADER_ID, null, loaderFromInternet);
        } else {
            getLoaderManager().initLoader(LOAD_FROM_DB_LOADER_ID, null, loaderFromInternet);
        }
    }


    public String combineURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_WEATHER);
        for (int i = 0; i < citiesID.size()-1; i++) {
            sb.append(citiesID.get(i)).append(",");
        }
        sb.append(citiesID.get(citiesID.size()-1));
        sb.append(URL_PART_WITH_KEY);
        return sb.toString();
    }


}
