package com.udacity.pathfinder.android.udacitypathfinder.data;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import timber.log.Timber;

public class ParseClient {

  private static <T extends ParseObject> ParseQuery<T> buildArticleQuery(ParseQuery<T> query) {
    query.orderByDescending(ParseConstants.PARSE_COL_CREATED_AT);
    return query;
  }

  private static <T extends ParseObject> ParseQuery<T> buildNanodegreeQuery(ParseQuery<T> query) {
    query.orderByDescending(ParseConstants.PARSE_COL_DEGREE_ID);
    return query;
  }

  /* Does not check remote datastore if object is not found locally*/
  public static <T extends ParseObject> void request(
      String className, boolean fromLocal, String objectId, final RequestCallback2<T> callback) {
    ParseQuery<T> query = ParseQuery.getQuery(className);
    if (fromLocal) query.fromLocalDatastore();
    query.getInBackground(objectId, new GetCallback<T>() {
      @Override public void done(T object, ParseException e) {
        callback.onResponse(object, e);
      }
    });
  }

  public static <T extends ParseObject> void request(
      String className, boolean fromLocal, RequestCallback<T> callback) {
    ParseQuery<T> query = ParseQuery.getQuery(className);
    Log.d("ParseClient", className);
    switch (className) {
      case ParseConstants.ARTICLE_CLASS_NAME:
        request(className, fromLocal, callback, buildArticleQuery(query));
        break;
      case ParseConstants.NANODEGREE_CLASS_NAME:
        request(className, fromLocal, callback, buildNanodegreeQuery(query) );
      default:
        Timber.d("Invalid class name: " + className);
        break;
    }
  }

  private static <T extends ParseObject> void request(
      String className, boolean fromLocal, RequestCallback<T> callback, ParseQuery<T> query) {
    if (fromLocal) {
      requestFromLocal(className, callback, query);
    } else {
      requestFromRemote(className, callback, query);
    }
  }

  private static <T extends ParseObject> void requestFromLocal(
      final String className, final RequestCallback<T> callback, final ParseQuery<T> query) {
    query.fromLocalDatastore();
    query.findInBackground(new FindCallback<T>() {
      @Override public void done(List<T> objects, ParseException e) {
        if (e == null && objects != null) {
          Timber.d(objects.size() + " results found in local datastore for " + className);
          if (objects.isEmpty()) {
            // We must build a new query for the remote datastore
            ParseQuery<T> remoteQuery = ParseQuery.getQuery(className);
            requestFromRemote(className, callback, buildArticleQuery(remoteQuery));
          } else {
            callback.onResponse(objects, e);
          }
        } else {
          callback.onResponse(objects, e);
        }
      }
    });
  }

  private static <T extends ParseObject> void requestFromRemote(
      final String className, final RequestCallback<T> callback, ParseQuery<T> query) {
    query.findInBackground(new FindCallback<T>() {
      @Override public void done(final List<T> objects, ParseException e) {
        if (e == null && objects != null) {
          Timber.d(objects.size() + " results found in remote datastore for " + className);
          unpinAndRepin(className, callback, objects);
        } else {
          callback.onResponse(objects, e);
        }
      }
    });
  }

  private static <T extends ParseObject> void unpinAndRepin(
      final String className, final RequestCallback<T> callback, final List<T> objects) {
    ParseObject.unpinAllInBackground(className, new DeleteCallback() {
      @Override public void done(ParseException e) {
        if (e == null) {
          ParseObject.pinAllInBackground(className, objects, new SaveCallback() {
            @Override public void done(ParseException e) {
              callback.onResponse(objects, e);
            }
          });
        } else {
          callback.onResponse(objects, e);
        }
      }
    });
  }
}
