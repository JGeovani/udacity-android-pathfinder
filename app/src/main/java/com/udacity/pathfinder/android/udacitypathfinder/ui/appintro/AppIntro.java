package com.udacity.pathfinder.android.udacitypathfinder.ui.appintro;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.auth.CheckLogin;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.CacheNanodegreeImageAssets;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

public class AppIntro extends com.github.paolorotolo.appintro.AppIntro {

  // Please DO NOT override onCreate. Use init
  @Override
  public void init(Bundle savedInstanceState) {
    // check if user has already login
    checkLogin();

    // Add your slide's fragments here
    // AppIntro will automatically generate the dots indicator and buttons.
    addSlide(SlideFragment.newInstance(R.layout.fragment_slide1));
    addSlide(SlideFragment.newInstance(R.layout.fragment_slide2));
    addSlide(SlideFragment.newInstance(R.layout.fragment_slide3));
    addSlide(SlideFragment.newInstance(R.layout.fragment_slide4));
    // Instead of fragments, you can also use our default slide
    // Just set a title, description, background and image. AppIntro will do the rest
//    addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));

    // OPTIONAL METHODS
    // Override bar/separator color
    setSeparatorColor(Color.parseColor("#FFFFFF"));

    // Hide Skip/Done button
    showSkipButton(false);
    showDoneButton(true);

    // Turn vibration on and set intensity
    // NOTE: you will probably need to ask VIBRATE permission in Manifest
    setVibrate(true);
    setVibrateIntensity(50);
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
