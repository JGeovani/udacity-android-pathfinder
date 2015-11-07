package com.udacity.pathfinder.android.udacitypathfinder.auth;

//import org.parceler.Parcel;

/**
 * https://github.com/johncarl81/parceler
 * http://www.javacreed.com/gson-annotations-example/
 * http://square.github.io/retrofit/
 */

public class UdacityResponses {

//  @Parcel
  public static class SessionResponse {
    public Account account;
    public Session session;

//    @Parcel
    public static class Account {
      public boolean registered;
      public String key;
    }

//    @Parcel
    public static class Session {
      public String id;
      public String expiration;
    }
  }


}

