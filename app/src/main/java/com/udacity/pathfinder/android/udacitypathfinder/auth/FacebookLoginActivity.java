package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class FacebookLoginActivity extends Activity {
  ParseUser parseUser;
  private String TAG = getClass().getSimpleName();
  List<String> faceBookPermissions = Arrays.asList("public_profile", "email");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_facebook_login);

    ParseFacebookUtils.logInWithReadPermissionsInBackground(this, faceBookPermissions,
        new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException err) {
        if (user == null) {
          Log.d(TAG, "User cancelled the Facebook login.");
          finish();
        } else if (user.isNew()) {
          Log.d(TAG, "User signed up and logged in through Facebook!");
          faceBookLoginRequest();
        } else {
          Log.d(TAG, "User logged in through Facebook!");
          getUserDetailsFromParse();
          ParseUser.getCurrentUser().pinInBackground();
          isLoginComplete(true);
        }
      }
    });
  }

  // Facebook onActivityResult
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
  }

  // test
  public void faceBookLoginRequest() {
    Log.d(TAG, "Make fb request");
    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
        new GraphRequest.GraphJSONObjectCallback() {
          @Override
          public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
            Log.d(TAG, "graphresponse complete\n" + jsonObject);
            if (jsonObject != null) {
              JSONObject userProfile = new JSONObject();

              try {
                userProfile.put("facebookId", jsonObject.getLong("id"));
                userProfile.put("name", jsonObject.getString("name"));
                userProfile.put("firstName", jsonObject.getString("first_name"));
                userProfile.put("lastName", jsonObject.getString("last_name"));

                if (jsonObject.getString("email") != null)
                  userProfile.put("email", jsonObject.getString("email"));

                // Save the user profile info in a user property
                parseUser = ParseUser.getCurrentUser();
                parseUser.put("profile", userProfile);
                parseUser.put("facebookId", String.valueOf(jsonObject.getLong("id")));
                parseUser.put("email", jsonObject.getString("email"));
                parseUser.put("name", jsonObject.getString("name"));
                parseUser.put("firstName", jsonObject.getString("first_name"));
                parseUser.put("lastName", jsonObject.getString("last_name"));
                parseUser.put("username", jsonObject.getString("email"));


                parseUser.saveInBackground();
                Log.d(TAG, "saved user to parse");

                // Show the user info
                saveNewUser();
              } catch (JSONException e) {
                Log.d(TAG, "Error parsing returned user data. " + e);
              }
            } else if (graphResponse.getError() != null) {
              switch (graphResponse.getError().getCategory()) {
                case LOGIN_RECOVERABLE:
                  Log.d(TAG, "Authentication error: " + graphResponse.getError());
                  break;

                case TRANSIENT:
                  Log.d(TAG, "Transient error. Try again. " + graphResponse.getError());
                  break;

                case OTHER:
                  Log.d(TAG, "Some other error: " + graphResponse.getError());
                  break;
              }
            }
          }
        });
    Bundle parameters = new Bundle();
    parameters.putString("fields", "id, email, name, first_name, last_name");
    request.setParameters(parameters);
    request.executeAsync();
  }

  // end test
  private void saveNewUser() {
    Log.d(TAG, "Saving new user");
    parseUser = ParseUser.getCurrentUser();
    JSONObject userProfile = parseUser.getJSONObject("profile");
    Log.d(TAG, userProfile.toString());
    try {
      parseUser.setUsername(userProfile.getString("email"));
      parseUser.setEmail(userProfile.getString("email"));
      if (!ParseFacebookUtils.isLinked(parseUser)) {
        Log.d(TAG, "Attempting to link facebook account");
        AccessToken token = AccessToken.getCurrentAccessToken();
        ParseFacebookUtils.linkInBackground(parseUser, token, new SaveCallback() {
          @Override
          public void done(ParseException ex) {
            if (ParseFacebookUtils.isLinked(parseUser)) {
              isLoginComplete(true);
              ParseUser.getCurrentUser().pinInBackground();
              finish();
              Log.e(TAG, "Success, user logged in with Facebook!");
            }
          }
        });
      } else {
        ParseUser.getCurrentUser().pinInBackground();
        isLoginComplete(true);
        Log.d(TAG, "Parse is already linked to Facebook account");
      }
    } catch (Exception err) {
      Log.d(TAG, "Error parsing saved user data.");
    }

  }

  private void isLoginComplete(boolean isComplete) {
    SharedPref sp = new SharedPref(getApplicationContext());
    sp.saveLogin(isComplete);
    finish();
  }

  private void getUserDetailsFromParse() {
    parseUser = ParseUser.getCurrentUser();
  }


}
