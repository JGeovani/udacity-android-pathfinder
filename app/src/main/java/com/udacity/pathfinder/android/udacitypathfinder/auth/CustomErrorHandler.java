package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.content.Context;
import android.util.Log;

import com.udacity.pathfinder.android.udacitypathfinder.R;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Retrofit Custom Error Handler
 */
public class CustomErrorHandler implements ErrorHandler {
  private final Context context;
  private String TAG = this.getClass().getSimpleName();

  public CustomErrorHandler(Context context) {
    this.context = context;
  }

  @Override
  public Throwable handleError(RetrofitError cause) {
    String errorDescription;

    if (cause.getKind() == RetrofitError.Kind.NETWORK) {
      errorDescription = context.getString(R.string.error_network);
    } else {
      if (cause.getResponse() == null) {
        errorDescription = context.getString(R.string.error_no_response);
      } else {

        // Error message handling - return a simple error to Retrofit handlers..
        try {
          ErrorResponse errorResponse = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
          errorDescription = errorResponse.error;
        } catch (Exception ex) {
          try {
            errorDescription = context.getString(R.string.error_network_http_error,
                cause.getResponse().getStatus());
          } catch (Exception ex2) {
            Log.e(TAG, "handleError: " + ex2.getLocalizedMessage());
            errorDescription = context.getString(R.string.error_unknown);
          }
        }
      }
    }

    return new Exception(errorDescription);
  }


  public class ErrorResponse {
    String error;
  }

}