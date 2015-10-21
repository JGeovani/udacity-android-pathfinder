package com.udacity.pathfinder.android.udacitypathfinder.data;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * This class reacts to Parse push notifications.
 */
public class ParseBroadcastReceiver extends ParsePushBroadcastReceiver {

  /**
   * This method is called when a push notification is received. Depending on the incoming Intent,
   * this is either a notification to be displayed to the user or a notification informing us that
   * new articles have been added to the remote datastore. In the case of the latter, we launch a
   * service for retrieving new articles and adding them to the local datastore.
   */
  @Override protected void onPushReceive(Context context, Intent intent) {
    // For now, we launch the article service in response to all incoming notifications (for testing
    // purposes). The Parse job for retrieving new articles will send a push notification when
    // articles have been successfully committed to the datastore. The Intent extra will contain the
    // JSON payload of the notification.
    Intent serviceIntent = new Intent(context, RequestArticleService.class);
    context.startService(serviceIntent);
  }
}
