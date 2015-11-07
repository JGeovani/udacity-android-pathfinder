package com.udacity.pathfinder.android.udacitypathfinder.auth;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 *  REST adapter for a given class/interface.
 */

public class UdacityRestClient {

  private static final String PRODUCTION_URL = "https://www.udacity.com/api/";

  private UdacityRestClient() {
  }

  public static <S> S loginService(Class<S> serviceClass, Context context) {

    RestAdapter.Builder builder = new RestAdapter.Builder()
        .setLogLevel(RestAdapter.LogLevel.NONE)
        .setEndpoint(PRODUCTION_URL)
        .setErrorHandler(new CustomErrorHandler(context))
        .setClient(new OkClient(new OkHttpClient()));

    RestAdapter restAdapter = builder.build();
    return restAdapter.create(serviceClass);
  }

}

