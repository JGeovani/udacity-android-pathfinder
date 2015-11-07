package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

public class AuthCompatActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkLogin();
  }

  // extend this class to verify authentication in the event the user logs out
  private void checkLogin() {
    SharedPref sp = new SharedPref(this);
    if (!sp.readLoginStatus()) {
      Intent i = new Intent(this, LoginActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      startActivity(i);
    }
  }


}
