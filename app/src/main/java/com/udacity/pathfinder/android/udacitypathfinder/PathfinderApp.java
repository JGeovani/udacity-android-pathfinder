package com.udacity.pathfinder.android.udacitypathfinder;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.udacity.pathfinder.android.udacitypathfinder.parse.Article;

public class PathfinderApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // Register sub Classes
    ParseObject.registerSubclass(Article.class);
    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);
    // Initialize Parse
    Parse.initialize(this, "m5DPjE3I2Aghdmu49nZcdnsLUkeZFN84jtETLYHu", "GdF3lzXZL3zPF1T1DwDB2G9I1YOBDdLaLPAFs3GY");
    // Allow anonymous Users to enable local datastore without having to log in
    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
