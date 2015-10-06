package com.udacity.pathfinder.android.udacitypathfinder;

import android.app.Application;

import com.parse.Parse;

public class PathfinderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // Initialize Parse
        Parse.initialize(this, "m5DPjE3I2Aghdmu49nZcdnsLUkeZFN84jtETLYHu", "GdF3lzXZL3zPF1T1DwDB2G9I1YOBDdLaLPAFs3GY");
    }
}
