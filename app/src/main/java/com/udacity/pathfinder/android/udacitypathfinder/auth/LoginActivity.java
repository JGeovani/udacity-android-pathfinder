package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

import java.util.Locale;

public class LoginActivity extends FragmentActivity implements View.OnClickListener {
  private String TAG = getClass().getSimpleName();
  private Button btn_google, btn_facebook, btn_udacity, btn_twitter, btn_LoginIn, btn_SignUp, btn_ForgetPass;
  private EditText et_username, et_password;
  ParseUser parseUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    et_username = (EditText) findViewById(R.id.et_login_username_input);
    et_password = (EditText) findViewById(R.id.et_login_password_input);
    btn_LoginIn = (Button) findViewById(R.id.btn_login_button);
    btn_SignUp = (Button) findViewById(R.id.btn_signup_button);
    btn_ForgetPass = (Button) findViewById(R.id.btn_forgot_login);
    btn_google = (Button) findViewById(R.id.btn_google_login);
    btn_facebook = (Button) findViewById(R.id.btn_facebook_login);
    btn_udacity = (Button) findViewById(R.id.btn_udacity_login);
    btn_twitter = (Button) findViewById(R.id.btn_twitter_login);
    btn_LoginIn.setOnClickListener(this);
    btn_SignUp.setOnClickListener(this);
    btn_ForgetPass.setOnClickListener(this);
    btn_google.setOnClickListener(this);
    btn_facebook.setOnClickListener(this);
    btn_udacity.setOnClickListener(this);
    btn_twitter.setOnClickListener(this);
    // Prevent Keyboard from appearing
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

  }


  @Override
  public void onClick(View v) {
    FragmentManager fm = getFragmentManager();
    switch (v.getId()) {
      case R.id.btn_udacity_login:
        final UdacityLoginDialogFragment udacityFragment = new UdacityLoginDialogFragment();
        udacityFragment.setRetainInstance(true);
        udacityFragment.show(fm, "udacityLoginFragment");
        break;
      case R.id.btn_google_login:
        Intent gi = new Intent(this, GoogleLoginActivity.class);
        startActivity(gi);
        break;
      case R.id.btn_twitter_login:
        Intent ti = new Intent(this, TwitterLogin.class);
        startActivity(ti);
        break;
      case R.id.btn_facebook_login:
        Intent fi = new Intent(this, FacebookLoginActivity.class);
        startActivity(fi);
        break;
      case R.id.btn_login_button:
        attemptLogin();
        break;
      case R.id.btn_forgot_login:
        final ForgetParsePasswordDialogFragment forgotPasswordFragment = new ForgetParsePasswordDialogFragment();
        forgotPasswordFragment.setRetainInstance(true);
        forgotPasswordFragment.show(fm, "ForgotPasswordFragment");
        break;
      case R.id.btn_signup_button:
        Intent isu = new Intent(this, SignUpActivity.class);
        startActivity(isu);
        break;
    }
  }

  public void attemptLogin() {

    clearErrors();

    // Store values at the time of the login attempt.
    String username = et_username.getText().toString();
    String password = et_password.getText().toString();
    View focusView = null;
    boolean cancel = false;


    // Check for a valid password.
    if (TextUtils.isEmpty(password)) {
      et_password.setError(getString(R.string.global_error_valid_password_required_field));
      focusView = et_password;
      cancel = true;
    } else if (password.length() < 4) {
      et_password.setError(getString(R.string.global_error_valid_password_required_field));
      focusView = et_password;
      cancel = true;
    }

    // Check for a valid email address.
    FieldValidator emailValidate = new FieldValidator(true, false);

    if (TextUtils.isEmpty(username)) {
      et_username.setError(getString(R.string.global_error_valid_email_required_field));
      focusView = et_username;
      cancel = true;
    } else if (!emailValidate.validate(username)) {
      et_username.setError(getString(R.string.global_error_valid_email_required_field));
      focusView = et_username;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // perform the user login attempt.
      login(username.toLowerCase(Locale.getDefault()), password);
    }
  }

  private void login(String username, String password) {
    ParseUser.logInInBackground(username, password, new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if (e == null) {
          ParseUser.getCurrentUser().pinInBackground();
          isLoginComplete(true);
        } else
          loginUnSuccessful();
      }
    });
  }

  protected void loginUnSuccessful() {
    alertUser("Login", "Username or Password is invalid.");
  }

  private void alertUser(String title, String message) {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(title);
    alertDialog.setMessage(message);
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.global_ok_button_label),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    alertDialog.show();
  }

  private void isLoginComplete(boolean isComplete) {
    SharedPref sp = new SharedPref(getApplicationContext());
    sp.saveLogin(isComplete);
    finish();
  }

  private void clearErrors() {
    et_username.setError(null);
    et_password.setError(null);
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "resumed");
    SharedPref sp = new SharedPref(this);
    if (sp.readLoginStatus()) {
      finish();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "resumed");
  }

}
