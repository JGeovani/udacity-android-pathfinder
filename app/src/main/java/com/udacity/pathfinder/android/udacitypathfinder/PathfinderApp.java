package com.udacity.pathfinder.android.udacitypathfinder;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Nanodegree;

import timber.log.Timber;

import static timber.log.Timber.DebugTree;

public class PathfinderApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.plant(new DebugTree());
    }

    // Register sub Classes
    ParseObject.registerSubclass(Article.class);
    ParseObject.registerSubclass(Nanodegree.class);
    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);
    // Initialize Parse
    Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
    ParseInstallation.getCurrentInstallation().saveInBackground();
    // Enable Parse Debugging
    Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
    // Facebook Authentication Initialization
    ParseFacebookUtils.initialize(this);
    // Twitter Authentication Initialization
    ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
    // Check if we have a current user already cached, if not enable automatic parse user
    if (ParseUser.getCurrentUser() == null) {
      // Allow anonymous Users to enable local datastore without having to log in
      ParseUser.enableAutomaticUser();
      ParseUser.getCurrentUser().saveInBackground();
      Log.d("PARSE", "creating auto user " + ParseUser.getCurrentUser().getUsername());
    } else {
      Log.d("PARSE", "Already have a user cached " + ParseUser.getCurrentUser().getUsername());
    }
    ParseACL defaultACL = new ParseACL();
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
