package com.udacity.pathfinder.android.udacitypathfinder.auth;


import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.udacity.pathfinder.android.udacitypathfinder.R;

public class ForgetParsePasswordDialogFragment extends DialogFragment {
  EditText et_forgetpassword = null;
  Button btn_submitforgetpassword = null;
  String password = null;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_forgot_password, container);

    et_forgetpassword = (EditText) view.findViewById(R.id.et_forgetpassword);
    btn_submitforgetpassword = (Button) view.findViewById(R.id.btn_submitforgetpassword);

    btn_submitforgetpassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        password = et_forgetpassword.getText().toString();
        checkEmailID();
      }
    });

    getDialog().setTitle("Password Recovery");
    return view;
  }

  protected void checkEmailID() {
    if (TextUtils.isEmpty(password)) {
      et_forgetpassword.setError(getString(R.string.global_error_valid_email_required_field));
    } else if (!password.contains("@")) {
      et_forgetpassword.setError(getString(R.string.global_error_valid_email_required_field));
    } else
      forgotPassword(password);
  }

  public void forgotPassword(String email) {
    //postEvent(new UserForgotPasswordStartEvent());
    ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {

      @Override
      public void done(ParseException e) {
        if (e == null) {
          Toast.makeText(getActivity(),
              "Successfully sent link to your email for reset Password", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(getActivity(),
              "Failed to send link to your email for reset Password", Toast.LENGTH_LONG).show();

        }
        dismiss();
      }
    });
  }

}
