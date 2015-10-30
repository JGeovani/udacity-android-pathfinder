package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;
import com.udacity.pathfinder.android.udacitypathfinder.ui.feed.FeedActivity;

public class CheckLogin extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPref sp = new SharedPref(this);
    if (!sp.readLoginStatus()) {
      Intent i = new Intent(this, LoginActivity.class);
      startActivity(i);
    } else {
      Intent i = new Intent(this, FeedActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
      startActivity(i);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    finish();
  }
}
