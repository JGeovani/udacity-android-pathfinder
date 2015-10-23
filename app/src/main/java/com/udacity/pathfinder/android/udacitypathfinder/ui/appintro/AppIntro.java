package com.udacity.pathfinder.android.udacitypathfinder.ui.appintro;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;
import com.udacity.pathfinder.android.udacitypathfinder.ui.feed.FeedActivity;

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
    setBarColor(Color.parseColor("#373737"));
    setSeparatorColor(Color.parseColor("#FFFFFF"));

    // Hide Skip/Done button
    showSkipButton(true);
    showDoneButton(true);

    // Turn vibration on and set intensity
    // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
    setVibrate(true);
    setVibrateIntensity(30);
  }


  public void onSkipPressed() {
    startArticleFeed();
  }

  @Override
  public void onDonePressed() {
    startArticleFeed();
  }

  private void checkLogin() {
    SharedPref sp = new SharedPref(this);
    if (sp.readLoginStatus()) {
      startArticleFeed();
    }
  }

  private void startArticleFeed(){
    Intent i = new Intent(this, FeedActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(i);
  }
}