package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Form validator
 */
public class FieldValidator {
  private Pattern pattern;
  private Matcher matcher;
  private boolean isEmail, isPassword;

  private static final String EMAIL_PATTERN =
      "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
          "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|" +
          "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*" +
          "[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|" +
          "[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]" +
          ":(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\" +
          "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
  private static final String PASSWORD_PATTERN =
      "^.{5,}$";

  public FieldValidator(boolean isEmail, boolean isPassword) {
    this.isEmail = isEmail;
    this.isPassword = isPassword;
    if (isEmail)
      pattern = Pattern.compile(EMAIL_PATTERN);
    if (isPassword)
      pattern = Pattern.compile(PASSWORD_PATTERN);
  }

  /**
   * Validate hex with regular expression
   *
   * @param hex hex for validation
   * @return true valid hex, false invalid hex
   */
  public boolean validate(final String hex) {
    matcher = pattern.matcher(hex);
    if (isEmail)
      Log.d("Validating email : " + hex, String.valueOf(matcher.matches()));
    if (isPassword)
      Log.d("Validating password : " + hex, String.valueOf(matcher.matches()));
    return matcher.matches();
  }
}

