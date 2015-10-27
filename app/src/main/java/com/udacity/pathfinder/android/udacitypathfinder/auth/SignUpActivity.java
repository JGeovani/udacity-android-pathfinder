package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

import java.util.Locale;

public class SignUpActivity extends Activity implements View.OnClickListener {
  private EditText mEmailEditText, mPasswordEditText, mConfirmPasswordEditText, mLastNameEditText, mFirstNameEditText;
  private Button mCreateAccountButton;
  private String mEmail, mPassword, mConfirmPassword, mFirstName, mLastName;
  int errorCode = 0;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    this.setFinishOnTouchOutside(true);
    mEmailEditText = (EditText) findViewById(R.id.etEmail);
    mFirstNameEditText = (EditText) findViewById(R.id.etPassword);
    mLastNameEditText = (EditText) findViewById(R.id.etPassword);
    mPasswordEditText = (EditText) findViewById(R.id.etPassword);
    mConfirmPasswordEditText = (EditText) findViewById(R.id.etPasswordConfirm);
    mCreateAccountButton = (Button) findViewById(R.id.btnCreateAccount);
    mCreateAccountButton.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btnCreateAccount:
        createAccount();
        break;

      default:
        break;
    }
  }

  private void createAccount() {
    clearErrors();

    boolean cancel = false;
    View focusView = null;

    // Store values at the time of the login attempt.
    mEmail = mEmailEditText.getText().toString();
    mPassword = mPasswordEditText.getText().toString();
    mConfirmPassword = mConfirmPasswordEditText.getText().toString();
    mFirstName = mFirstNameEditText.getText().toString();
    mLastName = mLastNameEditText.getText().toString();

    // Check for a valid confirm password.
    if (TextUtils.isEmpty(mFirstName)) {
      mFirstNameEditText.setError(getString(R.string.global_error_required_field));
      focusView = mFirstNameEditText;
      cancel = true;
    }
    // Check for a valid password.
    if (TextUtils.isEmpty(mLastName)) {
      mLastNameEditText.setError(getString(R.string.global_error_required_field));
      focusView = mLastNameEditText;
      cancel = true;
    }
    // Check for a valid confirm password.
    if (TextUtils.isEmpty(mConfirmPassword)) {
      mConfirmPasswordEditText.setError(getString(R.string.global_hint_confirm_password));
      focusView = mConfirmPasswordEditText;
      cancel = true;
    } else if (mPassword != null && !mConfirmPassword.equals(mPassword)) {
      mPasswordEditText.setError(getString(R.string.global_hint_confirm_password));
      focusView = mPasswordEditText;
      cancel = true;
    }
    // Check for a valid password.
    if (TextUtils.isEmpty(mPassword)) {
      mPasswordEditText.setError(getString(R.string.global_hint_password));
      focusView = mPasswordEditText;
      cancel = true;
    } else if (mPassword.length() < 4) {
      mPasswordEditText.setError(getString(R.string.global_hint_password));
      focusView = mPasswordEditText;
      cancel = true;
    }

    // Check for a valid email address.
    if (TextUtils.isEmpty(mEmail)) {
      mEmailEditText.setError(getString(R.string.global_hint_email));
      focusView = mEmailEditText;
      cancel = true;
    } else if (!mEmail.contains("@")) {
      mEmailEditText.setError(getString(R.string.global_hint_email));
      focusView = mEmailEditText;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      signUp(mEmail.toLowerCase(Locale.getDefault()), mEmail, mFirstName, mLastName, mPassword);
    }
  }

  private void signUp(final String mUsername, String mEmail, String mFirstName, String mLastName, final String mPassword) {
    ParseUser user = new ParseUser();
    user.setUsername(mUsername);
    user.setPassword(mPassword);
    user.setPassword(mFirstName);
    user.setPassword(mLastName);
    user.setEmail(mEmail);
    user.signUpInBackground(new SignUpCallback() {
      public void done(ParseException e) {
        if (e == null) {
          ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
              if (e == null) {
                errorCode = 0;
                ParseUser.getCurrentUser().pinInBackground();
                signUpMsg("Account Created Successfully", true);
              } else {
                errorCode = 1;
                Log.e("PARSE ERROR: ", e.toString());
                signUpMsg("Invalid user name or password", false);
                isLoginComplete(false);
              }
            }
          });
        } else {
          // Sign up didn't succeed. Look at the ParseException
          // to figure out what went wrong
          errorCode = 2;
          Log.e("PARSE ERROR: ", e.toString());
          signUpMsg("Account already exist", false);
        }
      }
    });
  }

  protected void signUpMsg(String msg, boolean isComplete) {
    if (isComplete){
      isLoginComplete(true);
    }else {
      isLoginComplete(false);
    }
      AlertDialog alertDialog = new AlertDialog.Builder(this).create();
      alertDialog.setMessage(msg);
    if (errorCode == 2){
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.global_try_again_button_label),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            clearFields();
            dialog.dismiss();
          }
        });
      alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.global_cancel_button_label),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            finish();
          }
        });
    } else {
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.global_ok_button_label),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            if (errorCode == 0) {
              finish();
            }
          }
        });
    }
      alertDialog.setCanceledOnTouchOutside(false);
      alertDialog.show();
  }

  private void clearErrors() {
    mEmailEditText.setError(null);
    mPasswordEditText.setError(null);
    mConfirmPasswordEditText.setError(null);
  }

  private void clearFields(){
    mEmailEditText.setText("");
    mPasswordEditText.setText("");
    mConfirmPasswordEditText.setText("");
  }

  private void isLoginComplete(boolean isComplete) {
    SharedPref sp = new SharedPref(getApplicationContext());
    sp.saveLogin(isComplete);
  }

}