package com.udacity.pathfinder.android.udacitypathfinder.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
  private static final String SHARED_PREF_NAME = "pathfinder";
  private static final String LOGIN_COMPLETE = "login_complete";
  private static final String USER_ID = "user_id";

  private Context context;
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor editor;

  public SharedPref(Context ctx) {
    this.context = ctx;
    sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, 0);
    editor = sharedPreferences.edit();
  }

  public void saveLogin(boolean isLoginComplete) {
    editor.putBoolean(LOGIN_COMPLETE, isLoginComplete);
    editor.apply();
  }

  public void saveLogin(boolean isLoginComplete, String userId) {
    editor.putBoolean(LOGIN_COMPLETE, isLoginComplete);
    editor.putString(USER_ID, userId);
    editor.apply();
  }

  public boolean readLoginStatus() {
    return sharedPreferences.getBoolean(LOGIN_COMPLETE, false);
  }

  public String getUserId() {
    return sharedPreferences.getString(USER_ID, "default");
  }

}