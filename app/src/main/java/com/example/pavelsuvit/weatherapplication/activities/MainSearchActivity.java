package com.example.pavelsuvit.weatherapplication.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pavelsuvit.weatherapplication.R;

public class MainSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onSearchRequested();
    }
}
