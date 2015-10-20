package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Dialog Fragment to prompt end user for Udacity Login Credentials
 */
public class UdacityLoginDialogFragment extends DialogFragment implements View.OnClickListener {

  private String TAG = getClass().getSimpleName();
  private EditText et_username, et_password;
  private Button btn_submit, btn_cancel;
  ProgressDialog progressDialog;
  ParseUser parseUser;

  public UdacityLoginDialogFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_udacity_login, container);
    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setTitle("Verifying Credentials");
    progressDialog.setMessage("Please wait while we verify authentication");
    et_username = (EditText) view.findViewById(R.id.et_email_address);
    et_password = (EditText) view.findViewById(R.id.et_password);
    btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
    btn_submit = (Button) view.findViewById(R.id.btn_submit);
    btn_cancel.setOnClickListener(this);
    btn_submit.setOnClickListener(this);
    return view;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_cancel:
        this.dismiss();
        break;
      case R.id.btn_submit:
        udacityLoginRequest(et_username.getText().toString(), et_password.getText().toString());
        break;
    }

  }

  public void udacityLoginRequest(final String username, final String password) {
    // Validate username and password
    FieldValidator emailValidator = new FieldValidator(true, false);
    FieldValidator passwordValidator = new FieldValidator(false, true);
    if (!emailValidator.validate(username) || !passwordValidator.validate(password)) {
      alertUser(getString(R.string.udacity_login_required_fields));
    } else {

      progressDialog.show();
      String credentials = "{\"password\":\"" + password + "\",\"username\":\"" + username + "\"}";

      // Create Udacity REST Adapter
      UdacityApiService apiClient = UdacityRestClient.loginService(UdacityApiService.class, getActivity());

      //Fetch and print
      apiClient.signIn(credentials, new Callback<UdacityResponses.SessionResponse>() {

        @Override
        public void success(UdacityResponses.SessionResponse sessionResponse, Response response) {
          Log.d("Callback : ", "success\nRegistered:"
              + sessionResponse.account.registered
              + "\nKey:" + sessionResponse.account.key
              + "\nSession ID:" + sessionResponse.session.id
              + "\nSession Expires:" + sessionResponse.session.expiration);
//          parseUser.logOut();
          ParseUser newParseUser = new ParseUser();
          try {
            // Save the user profile info in a user property
            newParseUser.setUsername(username);
            newParseUser.setEmail(username);
            newParseUser.setPassword(password);
            newParseUser.signUpInBackground(new SignUpCallback() {
              @Override
              public void done(ParseException e) {
                if (e == null) {
                  Log.d(TAG, "udacity user signed up with parse");
                  // Next, login to obtain token
                  ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                      if (user != null) {
                        // Hooray! The user is logged in.
                        ParseUser.becomeInBackground(ParseUser.getCurrentUser().getSessionToken(),
                            new LogInCallback() {
                              @Override
                              public void done(ParseUser parseUser, ParseException e) {
                                Log.d(TAG, "udacity user is now signed into parse");
                              }
                            });
                      } else {
                        // Signup failed. Look at the ParseException in log to see what happened.
                        Log.d(TAG, e.getMessage());
                      }
                    }
                  });
                } else {
                  boolean isTaken = checkWord(String.valueOf(e));
                  if (isTaken) {
                    // User already exist.  Next, login to obtain token
                    Log.d(TAG, "udacity user already exist, attempting to signin with credintials");
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                      public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                          // Hooray! The user is logged in.
                          ParseUser.becomeInBackground(ParseUser.getCurrentUser().getSessionToken(),
                              new LogInCallback() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                  Log.d(TAG, "Existing udacity user is login to parse");
                                }
                              });
                        } else {
                          Log.d(TAG, e.getMessage());
                        }
                      }
                    });
                  }
                }
              }
            });
            SharedPref sp = new SharedPref(getActivity());
            sp.saveLogin(true);
            progressDialog.dismiss();
            getActivity().finish();
            dismiss();
            Log.d(TAG, "saved udacity user to parse");

          } catch (Exception err) {
            Log.e(TAG, err.getMessage());
          }
        }

        @Override
        public void failure(RetrofitError error) {
          String message = "Account not found or invalid credentials.";
          Log.d("Callback : ", "failed : " + error.getLocalizedMessage() + "\n" + message);
          alertUser(error.getLocalizedMessage());
          progressDialog.dismiss();
        }
      });
    }
  }

  private void alertUser(String message) {
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(getString(R.string.udacity_login_required_fields_title));
    alertDialog.setMessage(message);
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.global_ok_button_label),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    alertDialog.show();
  }

  private static String[] ERROR_WORDS = new String[]{
      "already taken"
  };

  public boolean checkWord(String input) {
    return Arrays.binarySearch(ERROR_WORDS, input) < 0; // Not found
  }


}
