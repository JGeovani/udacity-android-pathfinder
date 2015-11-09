package com.udacity.pathfinder.android.udacitypathfinder.ui.appintro;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.auth.CheckLogin;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.CacheNanodegreeImageAssets;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

public class AppIntro extends com.github.paolorotolo.appintro.AppIntro {

  // Please DO NOT override onCreate. Use init
  @Override
  public void init(Bundle savedInstanceState) {
      checkLogin();
      addSlide(SlideFragment.newInstance(R.layout.fragment_slide1));
      addSlide(SlideFragment.newInstance(R.layout.fragment_slide2));
      addSlide(SlideFragment.newInstance(R.layout.fragment_slide3));
      addSlide(SlideFragment.newInstance(R.layout.fragment_slide4));

      setSeparatorColor(Color.parseColor("#FFFFFF"));
      TextView separator = (TextView) findViewById(R.id.bottom_separator);
      separator.getLayoutParams().height = 3;

      showSkipButton(false);
      showDoneButton(true);
      setVibrate(true);
      setVibrateIntensity(35);
  }


  public void onSkipPressed() {
    startCheckLogin();
  }

  @Override
  public void onDonePressed() {
    startCheckLogin();
  }

  private void checkLogin() {
    SharedPref sp = new SharedPref(this);
    if (sp.readLoginStatus()) {
      startCheckLogin();
    }
  }

  private void startCheckLogin() {
    Intent i = new Intent(this, CheckLogin.class);
    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(i);
    // load nanodegree images in background thread and cache
    new Thread(new CacheNanodegreeImageAssets(this)).start();
  }

}
