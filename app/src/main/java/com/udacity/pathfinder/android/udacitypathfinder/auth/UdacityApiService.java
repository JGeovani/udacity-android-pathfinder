package com.udacity.pathfinder.android.udacitypathfinder.auth;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * interface
 */
interface UdacityApiService {
  // Login existing account
  @FormUrlEncoded
  @POST("/session")
  void signIn(
    @Field("udacity") String udacity,
    Callback<UdacityResponses.SessionResponse> callback
  );
}
