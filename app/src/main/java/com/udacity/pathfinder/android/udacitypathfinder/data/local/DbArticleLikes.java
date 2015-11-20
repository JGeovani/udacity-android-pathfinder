package com.udacity.pathfinder.android.udacitypathfinder.data.local;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback2;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.LikedArticle;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Likes;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DbArticleLikes {

  private String TAG = getClass().getSimpleName();
  Context context;
  DbHelper dbHelper;
  SQLiteDatabase db;
  SharedPref sp;

  public DbArticleLikes(Context ctx) {
    this.context = ctx;
    dbHelper = new DbHelper(context);
    sp = new SharedPref(context);
  }

  public void addLike(String articleId, String[] nanodegrees, boolean isLiked) {
    db = dbHelper.getWritableDatabase();
    String username = sp.getUserId();
    Gson gson = new Gson();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    String date = df.format(Calendar.getInstance().getTime());
    ContentValues cv = new ContentValues();
    cv.put(DbHelper.COLUMN_ARTICLE_ID, articleId);
    cv.put(DbHelper.COLUMN_USERNAME, username);
    cv.put(DbHelper.COLUMN_ARTICLE_IS_LIKED, isLiked);
    cv.put(DbHelper.COLUMN_DATE_ADDED, date);
    cv.put(DbHelper.COLUMN_ARTICLE_NANODEGREES, gson.toJson(nanodegrees));
    db.insert(DbHelper.TABLE_ARTICLE_LIKES, null, cv);
    db.close();
    Log.d(TAG, "User: " + username + ", inserted article ID:" + articleId + " with tags: " + gson.toJson(nanodegrees) + " dateSubmitted: " + date);
  }

  public boolean alreadyLiked(String articleId) {
    db = dbHelper.getReadableDatabase();
    String[] column = {DbHelper.COLUMN_ID, DbHelper.COLUMN_ARTICLE_ID, DbHelper.COLUMN_USERNAME};
    Cursor cursor = db.query(DbHelper.TABLE_ARTICLE_LIKES, column, null, null, null, null, null);
    boolean doesExist = false;
    String username = sp.getUserId();
    if (cursor != null) {
      int articleIdIndex = cursor.getColumnIndex(DbSchema.COLUMN_ARTICLE_ID);
      int usernameIndex = cursor.getColumnIndex(DbSchema.COLUMN_USERNAME);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        String article_id = cursor.getString(articleIdIndex);
        String user_name = cursor.getString(usernameIndex);
        if (article_id.equals(articleId) && user_name.equals(username)) {
          doesExist = true;
        }
        cursor.moveToNext();
      }
      cursor.close();
    }
    db.close();
    return doesExist;
  }

  public boolean isLiked(String articleId) {
    db = dbHelper.getReadableDatabase();
    String[] column = {DbHelper.COLUMN_ID, DbHelper.COLUMN_ARTICLE_ID, DbHelper.COLUMN_USERNAME, DbHelper.COLUMN_ARTICLE_IS_LIKED};
    Cursor cursor = db.query(DbHelper.TABLE_ARTICLE_LIKES, column, null, null, null, null, null);
    boolean articleisLiked = false;
    String username = sp.getUserId();
    if (cursor != null) {
      int articleIdIndex = cursor.getColumnIndex(DbSchema.COLUMN_ARTICLE_ID);
      int usernameIndex = cursor.getColumnIndex(DbSchema.COLUMN_USERNAME);
      int isLikedIndex = cursor.getColumnIndex(DbSchema.COLUMN_ARTICLE_IS_LIKED);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        String article_id = cursor.getString(articleIdIndex);
        String user_name = cursor.getString(usernameIndex);
        boolean isLiked = cursor.getInt(isLikedIndex) > 0;
        if (article_id.equals(articleId) && user_name.equals(username) && isLiked) {
          articleisLiked = true;
        }
        cursor.moveToNext();
      }
      cursor.close();
    }
    db.close();
    return articleisLiked;
  }

  public void updateLike(String articleId, boolean isLiked) {
    db = dbHelper.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(DbSchema.COLUMN_ARTICLE_IS_LIKED, isLiked);
    String username = sp.getUserId();
    String whereClause = DbSchema.COLUMN_ARTICLE_ID + " = ? AND " + DbSchema.COLUMN_USERNAME + " = ?";
    String[] whereArgs = {articleId, username};
    db.update(DbHelper.TABLE_ARTICLE_LIKES, cv, whereClause, whereArgs);
    db.close();
    Log.d(TAG, "User: " + username + ", updated article " + articleId + " liked: " + isLiked);
  }


  public int totalCount() {
    db = dbHelper.getReadableDatabase();
    String[] column = {DbHelper.COLUMN_USERNAME, DbHelper.COLUMN_ARTICLE_IS_LIKED};
    Cursor cursor = db.query(DbHelper.TABLE_ARTICLE_LIKES, column, null, null, null, null, null);
    int count = 0;
    String username = sp.getUserId();
    if (cursor != null) {
      int usernameIndex = cursor.getColumnIndex(DbSchema.COLUMN_USERNAME);
      int isLikedIndex = cursor.getColumnIndex(DbSchema.COLUMN_ARTICLE_IS_LIKED);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        String user_name = cursor.getString(usernameIndex);
        boolean isLiked = cursor.getInt(isLikedIndex) > 0;
        if (user_name.equals(username) && isLiked) {
          count++;
        }
        cursor.moveToNext();
      }
      cursor.close();
    }
    db.close();
    return count;
  }


  public HashMap<String, Integer> getNanoScore() {
    db = dbHelper.getReadableDatabase();
    HashMap<String, Integer> scores = new HashMap<>();
    String[] column = {DbHelper.COLUMN_ID, DbHelper.COLUMN_ARTICLE_IS_LIKED, DbHelper.COLUMN_USERNAME, DbHelper.COLUMN_ARTICLE_NANODEGREES};
    Cursor cursor = db.query(DbHelper.TABLE_ARTICLE_LIKES, column, null, null, null, null, null);
    String username = sp.getUserId();
    if (cursor != null) {
      int userIdIndex = cursor.getColumnIndex(DbSchema.COLUMN_USERNAME);
      int isLikedIdIndex = cursor.getColumnIndex(DbSchema.COLUMN_ARTICLE_IS_LIKED);
      int nanoIdIndex = cursor.getColumnIndex(DbSchema.COLUMN_ARTICLE_NANODEGREES);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        try {
          String nanoJson = cursor.getString(nanoIdIndex);
          JSONArray nanoData = new JSONArray(nanoJson);
          String user_name = cursor.getString(userIdIndex);
          boolean isLiked = cursor.getInt(isLikedIdIndex) > 0;
          if (user_name.equals(username) && isLiked) {
            for (int i = 0; i < nanoData.length(); i++) {
              String nanoString = nanoData.get(i).toString();
              if (scores.containsKey(nanoString)) {
                int score = scores.get(nanoString) + 1;
                scores.put(nanoString, score);
              } else {
                scores.put(nanoString, 1);
              }
            }
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
        cursor.moveToNext();
      }
      cursor.close();
    }
    db.close();
    return scores;
  }

  public void updateParseArticleLikes(final String articeId, final boolean isLiked) {
    Log.e(TAG, "Artice Id = " + articeId + ", updating like status to " + isLiked);
    ParseQuery query = new ParseQuery(ParseConstants.ARTICLE_CLASS_NAME);
    try {
      int likeCount = 0;
      ParseObject object = query.get(articeId);
      if (object.get(ParseConstants.ARTICLES_COL_LIKES) != null)
        likeCount = (int) object.get(ParseConstants.ARTICLES_COL_LIKES);
      if (isLiked) {
        likeCount++;
      } else {
        likeCount--;
      }
      object.put(ParseConstants.ARTICLES_COL_LIKES, likeCount);
      object.saveInBackground();
      Log.d(TAG, "Like count for artice id: " + articeId + " = " + likeCount);

    } catch (ParseException e) {
      e.printStackTrace();
      Log.e(TAG, "There was an error with retrieving Parse query");
    }
    syncUserArticleLikes();
  }


  public void saveInBackground() {
    ArrayList<LikedArticle> la = getArticlesLiked();
    List<ParseObject> objList = new ArrayList<>();
    for (LikedArticle obj : la) {
      Likes newObject = new Likes();
      newObject.put(ParseConstants.LIKES_ARTICLE_ID, obj.getArticleId());
      newObject.put(ParseConstants.LIKES_ARTICLE_ISLIKED, obj.isLiked());
      newObject.put("username", sp.getUserId());
      objList.add(newObject);
      Log.d(TAG, "Adding id " + obj.getArticleId() + " , isLiked: " + obj.isLiked() + " to parse backend");
    }
    ParseObject.saveAllInBackground(objList, new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e == null) {
          Log.d(TAG, "Save in background success");
        } else {
          Log.e(TAG, "ERROR - " + e);
        }
      }
    });
  }

  public void syncUserArticleLikes() {
    final String userId = sp.getUserId();
    ParseQuery<Likes> query = ParseQuery.getQuery(ParseConstants.LIKES_CLASS_NAME);
    query.whereEqualTo("username", userId);
    query.findInBackground(new FindCallback<Likes>() {
      ArrayList<LikedArticle> localLikes = getArticlesLiked();

      @Override
      public void done(List<Likes> remoteLikes, ParseException e) {
        final List<ParseObject> objList = new ArrayList<>();
        String remoteArticleId;
        String localArticleId;
        boolean localIsLiked;
        boolean remoteIsLiked;
        //local likes loop
        int i = 0;
        for (LikedArticle localLike : localLikes) {
          boolean alreadyExist = false;
          i++;
          localArticleId = localLike.getArticleId();
          localIsLiked = localLike.isLiked();
          // remote likes loop
          int j = 0;
          for (Likes remoteLike : remoteLikes) {
            j++;
            remoteArticleId = remoteLike.getArticleId();
            remoteIsLiked = remoteLike.isLiked();
            if (localArticleId.equals(remoteArticleId)) {
              alreadyExist = true;
              //compare and update if needed
              if (localIsLiked != remoteIsLiked) {
                remoteLike.put(ParseConstants.LIKES_ARTICLE_ISLIKED, localIsLiked);
                objList.add(remoteLike);
              }
              break;
            }
          }
          if (!alreadyExist) {
            Likes newObject = new Likes();
            newObject.put(ParseConstants.LIKES_ARTICLE_ID, localArticleId);
            newObject.put(ParseConstants.LIKES_ARTICLE_ISLIKED, localIsLiked);
            newObject.put("username", userId);
            objList.add(newObject);
          }
        }
        if (objList.size() > 0) {
          ParseObject.saveAllInBackground(objList, new SaveCallback() {
            @Override
            public void done(ParseException e) {
              if (e == null) {
              } else {
                Log.e(TAG, "ERROR - " + e);
              }
            }
          });
        }
      }

    });
  }



  public void restoreUserArticleLikes() {
    final String userId = sp.getUserId();
    ParseQuery<Likes> query = ParseQuery.getQuery(ParseConstants.LIKES_CLASS_NAME);
    query.whereEqualTo("username", userId);
    query.findInBackground(new FindCallback<Likes>() {
      @Override
      public void done(List<Likes> remoteLikes, ParseException e) {
        for (Likes remoteLike : remoteLikes) {
          final String remoteArticleId = remoteLike.getArticleId();
          final boolean remoteIsLiked = remoteLike.isLiked();
          ParseClient.request(
            ParseConstants.ARTICLE_CLASS_NAME, true, remoteArticleId, new RequestCallback2<Article>() {
              @Override
              public void onResponse(Article article, ParseException e) {
                if (article != null) {
                  List<String> remoteNanodegrees = article.getNanodegrees();
                  String[] nanodegreeData = new String[remoteNanodegrees.size()];
                  for (int i = 0; i < remoteNanodegrees.size(); i++) {
                    nanodegreeData[i] = remoteNanodegrees.get(i);
                  }
                  addLike(remoteArticleId, nanodegreeData, remoteIsLiked);
                }
              }
            });
        }
      }
    });
    sp.setFirstRunComplete(true);
  }

  public ArrayList<LikedArticle> getArticlesLiked() {
    db = dbHelper.getReadableDatabase();
    ArrayList<LikedArticle> likedArticles = new ArrayList<>();
    String[] column = {DbHelper.COLUMN_ID, DbHelper.COLUMN_ARTICLE_ID, DbHelper.COLUMN_ARTICLE_IS_LIKED};
    Cursor cursor = db.query(DbHelper.TABLE_ARTICLE_LIKES, column, null, null, null, null, null);
    if (cursor != null) {
      int articleIdIndex = cursor.getColumnIndex(DbSchema.COLUMN_ARTICLE_ID);
      int isLikedIdIndex = cursor.getColumnIndex(DbSchema.COLUMN_ARTICLE_IS_LIKED);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        LikedArticle likedArticle = new LikedArticle();
        String article_id = cursor.getString(articleIdIndex);
        boolean isLiked = cursor.getInt(isLikedIdIndex) > 0;
        likedArticle.setArticleId(article_id);
        likedArticle.setIsLiked(isLiked);
        likedArticles.add(likedArticle);
        cursor.moveToNext();
      }
    }
    db.close();
    return likedArticles;
  }


}
