package com.udacity.pathfinder.android.udacitypathfinder.data;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.CacheNanodegreeImageAssets;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.DbArticleLikes;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Nanodegree;
import com.udacity.pathfinder.android.udacitypathfinder.ui.feed.FeedActivity;

import java.util.List;

/**
 * This service pulls new articles from the remote datastore.
 */
public class ArticlePullService extends IntentService {

  public ArticlePullService() {
    super("ArticlePullService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    requestNewArticles();
    requestNewNanodegrees();
    new DbArticleLikes(getBaseContext()).syncUserArticleLikes();
  }

  /**
   * This method checks the created date of the most recent article in the local datastore. It uses
   * this date for querying the remote datastore for new articles and pinning the results to the
   * local datastore.
   */
  private void requestNewArticles() {
    ParseQuery<Article> localQuery = ParseQuery.getQuery(ParseConstants.ARTICLE_CLASS_NAME);
    localQuery.fromLocalDatastore();
    localQuery.orderByDescending(ParseConstants.PARSE_COL_CREATED_AT);
    localQuery.getFirstInBackground(new GetCallback<Article>() {
      @Override
      public void done(Article article, ParseException e) {
        if (e == null && article != null) {
          ParseQuery<Article> remoteQuery = ParseQuery.getQuery(ParseConstants.ARTICLE_CLASS_NAME);
          remoteQuery.orderByDescending(ParseConstants.PARSE_COL_CREATED_AT);
          remoteQuery.whereGreaterThan(ParseConstants.PARSE_COL_CREATED_AT, article.getCreatedAt());
          // Only show approved articles use: remoteQuery.whereEqualTo(ParseConstants.ARTICLES_COL_APPROVED, true);
          remoteQuery.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> articles, ParseException e) {
              if (e == null && articles != null) {
                ParseObject.pinAllInBackground(ParseConstants.ARTICLE_CLASS_NAME, articles);
                // notification goes here
                int totalArticles = articles.size();
                String message;
                if (totalArticles > 1) {
                  message = totalArticles + "New articles have been added";
                } else {
                  message = totalArticles + "New article has been added";
                }
                Uri notifySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext());
                Intent notificationIntent = new Intent(getBaseContext(), FeedActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notification.setSmallIcon(R.drawable.ic_app_compass);
                notification.setColor(getResources().getColor(R.color.primary));
                notification.setContentTitle("Pathfinder Alert");
                notification.setContentText(message);
                notification.setContentIntent(pendingIntent);
                notification.setSound(notifySound);
                mNotificationManager.notify(102, notification.build());
              }
            }
          });
        }
      }
    });
  }

  /**
   * Same as requestNewArticles, This method checks the created date of the most recent nanodegree
   * in the local datastore. It uses this date for querying the remote datastore for new
   * nanodegrees and pinning the results to the local datastore.
   */
  private void requestNewNanodegrees() {
    // obtain latest nanodegree entry within local storage and retrieve date of entry
    ParseQuery<Nanodegree> localQuery = ParseQuery.getQuery(ParseConstants.NANODEGREE_CLASS_NAME);
    localQuery.fromLocalDatastore();
    localQuery.orderByDescending(ParseConstants.PARSE_COL_CREATED_AT);
    localQuery.getFirstInBackground(new GetCallback<Nanodegree>() {
      @Override
      public void done(Nanodegree nanodegree, ParseException e) {
        // now we have date of entry we will need to compare
        if (e == null && nanodegree != null) {
          ParseQuery<Nanodegree> remoteQuery = ParseQuery.getQuery(ParseConstants.NANODEGREE_CLASS_NAME);
          remoteQuery.orderByDescending(ParseConstants.PARSE_COL_CREATED_AT);
          remoteQuery.whereGreaterThan(ParseConstants.PARSE_COL_CREATED_AT, nanodegree.getCreatedAt());
          remoteQuery.findInBackground(new FindCallback<Nanodegree>() {
            @Override
            public void done(List<Nanodegree> nanodegrees, ParseException e) {
              // after comparing to see if we have new nanodegrees we then add new to localdataset
              if (e == null && nanodegrees != null) {
                ParseObject.pinAllInBackground(ParseConstants.NANODEGREE_CLASS_NAME, nanodegrees);
                // load nanodegree images and cache inbackground thread
                new Thread(new CacheNanodegreeImageAssets(getBaseContext())).start();
              }
            }
          });
        }
      }
    });
  }

}
