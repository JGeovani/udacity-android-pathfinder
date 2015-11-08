package com.udacity.pathfinder.android.udacitypathfinder.data;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This receiver reacts to Parse push notifications.
 */
public class ParsePushReceiver extends ParsePushBroadcastReceiver {

  private static final String ALERT_NEW_ARTICLES_ADDED = "newArticlesAdded";

  /**
   * This method is called when a push notification is received. Depending on the incoming Intent,
   * this is either a notification to be displayed to the user or a notification informing us that
   * new articles have been added to the remote datastore. In the case of the latter, we launch a
   * service for retrieving new articles and adding them to the local datastore.
   */
  @Override protected void onPushReceive(Context context, Intent intent) {
    try {
      JSONObject data = new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
      if (data.getString("alert").equals(ALERT_NEW_ARTICLES_ADDED)) {
        Intent serviceIntent = new Intent(context, ArticlePullService.class);
        context.startService(serviceIntent);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
