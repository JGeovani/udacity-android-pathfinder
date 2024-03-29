package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

import org.json.JSONObject;

public class GoogleLoginActivity extends Activity implements
  ActivityCompat.OnRequestPermissionsResultCallback,
  GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener {

  private final String TAG = getClass().getSimpleName();
  ProgressDialog progressDialog;
  ParseUser parseUser;
  private String gDisplayName, gEmail, gId, firstName, lastName, password;
  private static int RC_SIGN_IN = 0;
  private GoogleApiClient mGoogleApiClient;
  private boolean mIntentInProgress, mSignInClicked;
  private ConnectionResult mConnectionResult;
  /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
  private static final int RC_PERM_GET_ACCOUNTS = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    super.onCreate(savedInstanceState);
    progressDialog = new ProgressDialog(this);
    // setting layout view
    setContentView(R.layout.activity_google_login);

    // start google plus api
    mGoogleApiClient = new GoogleApiClient.Builder(this)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .addApi(Plus.API)
      .addScope(Plus.SCOPE_PLUS_LOGIN)
      .build();
    startLoginProcess();
  }

  protected void onStart() {
    Log.d(TAG, "Start");
    super.onStart();
    if (!isNetworkConnected()) {
      Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG).show();
      finish();
    } else {
      // Google Plus API
      progressDialog.setMessage("Communicating with Google");
      progressDialog.show();
      mGoogleApiClient.connect();
    }
  }

  protected void onStop() {
    Log.d(TAG, "Stop");
    // Google Plus API
    mGoogleApiClient.disconnect();
    super.onStop();

  }

  protected void onResume() {
    Log.d(TAG, "Resume");
    progressDialog.setMessage("Communicating with Google");
    progressDialog.show();
    super.onResume();
  }

  protected void onPause() {
    Log.d(TAG, "Pause");
    super.onPause();
  }

  @Override
  public void onConnected(Bundle bundle) {
    mSignInClicked = false;
    //Get user info
    getProfileInformation();
  }

  @Override
  public void onConnectionSuspended(int i) {
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    Log.e(TAG, "ConnectionFailed: " + connectionResult);
    if (!connectionResult.hasResolution()) {
      GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
      return;
    }
    if (!mIntentInProgress) {
      // store connection for later use
      mConnectionResult = connectionResult;
      if (mSignInClicked) {
        /**
         * The user has already clicked sign-in, we will attempt
         * to resolve all errors until the user is signed in
         * or, if they cancel.
         */
        resolveSignInError();
      }
    }
  }

  /**
   * Check if we have the GET_ACCOUNTS permission and request it if we do not.
   *
   * @return true if we have the permission, false if we do not.
   */
  private boolean checkAccountsPermission() {
    progressDialog.dismiss();
    final String perm = Manifest.permission.GET_ACCOUNTS;
    int permissionCheck = ContextCompat.checkSelfPermission(this, perm);
    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
      // We have the permission
      return true;
    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
      // Need to show permission rationale, display a snackbar and then request
      // the permission again when the snackbar is dismissed.
      AlertDialog alertDialog = new AlertDialog.Builder(this).create();
      alertDialog.setMessage(getString(R.string.contacts_permission_rationale));
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.global_ok_button_label),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            // Request the permission again.
            ActivityCompat.requestPermissions(GoogleLoginActivity.this,
              new String[]{perm},
              RC_PERM_GET_ACCOUNTS);
            dialog.dismiss();

          }
        });
      alertDialog.show();
      return false;
    } else {
      // No explanation needed, we can request the permission.
      ActivityCompat.requestPermissions(this,
        new String[]{perm},
        RC_PERM_GET_ACCOUNTS);
      return false;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String permissions[],
                                         @NonNull int[] grantResults) {
    Log.d(TAG, "onRequestPermissionsResult:" + requestCode);
    if (requestCode == RC_PERM_GET_ACCOUNTS) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Log.d(TAG, "GET_ACCOUNTS Permission Granted.");
        progressDialog.setMessage("Obtaining data...");
        progressDialog.show();
        getProfileInformation();
      } else {
        Log.e(TAG, "GET_ACCOUNTS Permission Denied.");
        finish();
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int responseCode, Intent intent) {
    Log.d(TAG, "ActivityResult: " + requestCode + ", Response Code = " + responseCode);
    if (requestCode == RC_SIGN_IN) {
      if (responseCode != RESULT_OK) {
        mSignInClicked = false;
        Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG).show();
        finish();
      }
      mIntentInProgress = false;
      if (!mGoogleApiClient.isConnecting()) {
        mGoogleApiClient.connect();
      }
    }
  }

  private void signInWithGplus() {
    progressDialog.dismiss();
    Log.d(TAG, "Sign in with Google+ started");
    if (!mGoogleApiClient.isConnecting()) {
      resolveSignInError();
    }
  }

  private void resolveSignInError() {
    Log.d(TAG, "Resolving Sign In error");
    try {
      if (mConnectionResult.hasResolution()) {
        try {
          mIntentInProgress = true;
          mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
          Log.d(TAG, "there is an error:  " + e);
          mIntentInProgress = false;
          mGoogleApiClient.connect();
        }
      }
    } catch (Exception err) {
      Log.e(TAG, "ERROR: " + String.valueOf(err));
    }
  }

  private void getProfileInformation() {
    Log.d(TAG, "Get profile information: started ");
    // Android M Permission Check
    if (checkAccountsPermission()) {
      Log.d(TAG, "Permission verified granted for Google+ access ");
    } else {
      Log.d(TAG, "Permission was denied for Google+ access ");
    }
    // Check Android API Version
    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    if (currentapiVersion < android.os.Build.VERSION_CODES.M) {
      progressDialog.setMessage("Obtaining data...");
      progressDialog.show();
    }
    try {
      if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String personName = currentPerson.getDisplayName();
        firstName = currentPerson.getName().getGivenName();
        lastName = currentPerson.getName().getFamilyName();
        gDisplayName = personName;
        gId = currentPerson.getId();
        gEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Md5Hasher hash = new Md5Hasher(gId);
        password = hash.getHash();
        Log.d(TAG, password);
        JSONObject userProfile = new JSONObject();
        userProfile.put("googleId", gId);
        userProfile.put("name", gDisplayName);
        userProfile.put("firstName", firstName);
        userProfile.put("lastName", lastName);
        userProfile.put("email", gEmail);
        parseUser = new ParseUser();
        parseUser.put("profile", userProfile);
        parseUser.put("googleId", gId);
        parseUser.put("email", gEmail);
        parseUser.put("name", gDisplayName);
        parseUser.put("firstName", firstName);
        parseUser.put("lastName", lastName);
        parseUser.put("username", gEmail);
        parseUser.setPassword(password);
        parseUser.setEmail(gEmail);
        parseUser.setUsername(gId);

        parseUser.signUpInBackground(new SignUpCallback() {
          public void done(ParseException e) {
            Log.d(TAG, "Google user signed up with parse");
            // Next, login to obtain token
            ParseUser.logInInBackground(gId, password, new LogInCallback() {
              public void done(ParseUser user, ParseException e) {
                if (user != null) {
                  ParseUser.becomeInBackground(ParseUser.getCurrentUser().getSessionToken(),
                    new LogInCallback() {
                      @Override
                      public void done(ParseUser parseUser, ParseException e) {
                        Log.d(TAG, "---> Google user is now signed into parse");
                        ParseUser.getCurrentUser().pinInBackground();
                        isLoginComplete(true, gId);
                      }
                    });
                } else {
                  // Signup failed. Look at the ParseException in log to see what happened.
                  Log.e(TAG, e.getMessage());
                  isLoginComplete(false, null);
                }
              }
            });
          }
        });
      } else {
        Toast.makeText(this, "Please check your data connection",
          Toast.LENGTH_LONG).show();
        finish();
      }
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
  }

  /**
   * handler allows delay for auto starting Google+"
   */
  Handler finishHandler = new Handler();
  Runnable finishRunnable = new Runnable() {
    @Override
    public void run() {
      signInWithGplus();
      mSignInClicked = true;
    }
  };

  private boolean isNetworkConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo() != null;
  }

  private void startLoginProcess() {
    finishHandler.postDelayed(finishRunnable, 360);
  }

  private void isLoginComplete(boolean isComplete, String userId) {
    SharedPref sp = new SharedPref(this);
    sp.saveLogin(isComplete, userId);
    progressDialog.dismiss();
    finish();
  }

}
