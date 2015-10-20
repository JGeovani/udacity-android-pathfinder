package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;


public class TwitterLogin extends Activity{
    ParseUser parseUser;
    ProgressDialog progressDialog;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        progressDialog = new ProgressDialog(this);

        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d(TAG, "User cancelled the Twitter login.");
                    finish();
                } else if (user.isNew()) {
                    Log.d(TAG, "User signed up and logged in through Twitter!");
                    twitterLoginRequest();
                } else {
                    Log.d(TAG, "User logged in through Twitter!");
                    twitterLoginRequest();
                }
            }
        });
    }

    public void twitterLoginRequest() {
        Log.d(TAG, "Make twitter request");
        String screenname, userid;
        screenname = ParseTwitterUtils.getTwitter().getScreenName();
        userid = ParseTwitterUtils.getTwitter().getUserId();
        JSONObject userProfile = new JSONObject();
        try {
            userProfile.put("twitterId", userid);
            userProfile.put("name", screenname);
            // Save the user profile info in a user property
            parseUser = ParseUser.getCurrentUser();
            parseUser.put("profile", userProfile);
            parseUser.put("twitterId", userid);
            parseUser.put("name", screenname);
            parseUser.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!ParseTwitterUtils.isLinked(parseUser)) {
            ParseTwitterUtils.link(parseUser, this, new SaveCallback() {
                @Override
                public void done(ParseException ex) {
                    if (ParseTwitterUtils.isLinked(parseUser)) {
                        Log.d(TAG, "Twitter user linked to parse and logged in with Twitter!");
                        ParseUser.getCurrentUser().pinInBackground();
                        isLoginComplete(true);
                    }
                }
            });
        }
        ParseUser.getCurrentUser().pinInBackground();
        isLoginComplete(true);
    }
    /**
     * handler used to prevent leak with "AndroidHttpClient created and never closed"
     * Creating a delay before finishing activity allows time to properlly close ParseTwitterUtils HttpClient
     */
    Handler finishHandler = new Handler();
    Runnable finishRunnable = new Runnable() {
        @Override
        public void run() {
            progressDialog.dismiss();
            finish();
        }
    };

    private void isLoginComplete(boolean isComplete) {
        SharedPref sp = new SharedPref(getApplicationContext());
        sp.saveLogin(isComplete);
        progressDialog.setMessage("Cleaning up...");
        progressDialog.show();
        finishHandler.postDelayed(finishRunnable, 1000);
    }

}
