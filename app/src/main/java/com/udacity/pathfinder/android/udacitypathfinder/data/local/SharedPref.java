package com.udacity.pathfinder.android.udacitypathfinder.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SharedPref {
  private static final String SHARED_PREF_NAME = "pathfinder";
  private static final String LOGIN_COMPLETE = "login_complete";
  private static final String USER_ID = "user_id";
  private static final String NANODEGREES = "nanodegrees";


  private Context context;
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor editor;
  Set<String> ndSet = new HashSet<>();

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

  public void saveNanodegree(ArrayList<String> nanodegree) {
    ndSet.isEmpty();
    for (int i = 0; i < nanodegree.size(); i++) {
      ndSet.add(nanodegree.get(i));
    }
    editor.putStringSet(NANODEGREES, ndSet);
    editor.apply();
  }

  public String[] getNanodegrees() {
    Set<String> set = sharedPreferences.getStringSet(NANODEGREES, null);
    String[] nano = new String[set.size()];
    int count = 0;
    for (String data : set) {
      nano[count] = data;
      count++;
    }
    return nano;
  }


  public boolean readLoginStatus() {
    return sharedPreferences.getBoolean(LOGIN_COMPLETE, false);
  }

  public String getUserId() {
    return sharedPreferences.getString(USER_ID, "default");
  }

}