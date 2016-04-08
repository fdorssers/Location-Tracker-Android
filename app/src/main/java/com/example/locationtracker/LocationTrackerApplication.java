package com.example.locationtracker;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by frank on 8-4-2016.
 */
public class LocationTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
