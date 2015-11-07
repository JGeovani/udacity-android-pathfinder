package com.udacity.pathfinder.android.udacitypathfinder.data.local;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DbArticleLikes {

  private String TAG = getClass().getSimpleName();
  Context context;
  DbHelper dbHelper;
  SQLiteDatabase db;
  SharedPref sp;

  public DbArticleLikes(Context ctx) {
    this.context = ctx;
    dbHelper = new DbHelper(context);
    db = dbHelper.getWritableDatabase();
    sp = new SharedPref(context);
  }

  public void addLike(String articleId, String[] nanodegrees) {
    String username = sp.getUserId();
    Gson gson = new Gson();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    String date = df.format(Calendar.getInstance().getTime());
    ContentValues cv = new ContentValues();
    cv.put(DbHelper.COLUMN_ARTICLE_ID, articleId);
    cv.put(DbHelper.COLUMN_USERNAME, username);
    cv.put(DbHelper.COLUMN_ARTICLE_IS_LIKED, true);
    cv.put(DbHelper.COLUMN_DATE_ADDED, date);
    cv.put(DbHelper.COLUMN_ARTICLE_NANODEGREES, gson.toJson(nanodegrees));
    db.insert(DbHelper.TABLE_ARTICLE_LIKES, null, cv);
    Log.d(TAG, "User: " + username + ", inserted article ID:" + articleId + " with tags: " + gson.toJson(nanodegrees) + " dateSubmitted: " + date);
  }

  public boolean alreadyLiked(String articleId) {
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
    }
    return doesExist;
  }

  public void deleteLike(String articleId) {
    String username = sp.getUserId();
    String whereClause = DbSchema.COLUMN_ARTICLE_ID + " = ? AND " + DbSchema.COLUMN_USERNAME + " = ?";
    String[] whereArgs = {articleId, username};
    db.delete(DbHelper.TABLE_ARTICLE_LIKES, whereClause, whereArgs);
    Log.d(TAG, "User: " + username + ", deleted article " + articleId);
  }

  public void updateLike(String articleId, boolean isLiked) {
    ContentValues cv = new ContentValues();
    cv.put(DbSchema.COLUMN_ARTICLE_IS_LIKED, isLiked);
    String username = sp.getUserId();
    String whereClause = DbSchema.COLUMN_ARTICLE_ID + " = ? AND " + DbSchema.COLUMN_USERNAME + " = ?";
    String[] whereArgs = {articleId, username};
    db.update(DbHelper.TABLE_ARTICLE_LIKES, cv, whereClause, whereArgs);
    Log.d(TAG, "User: " + username + ", updated article " + articleId + " liked: " + isLiked);
  }


  public int totalCount() {
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
        boolean isLiked = cursor.getInt(isLikedIndex)>0;
        if (user_name.equals(username)&&isLiked) {
          count++;
        }
        cursor.moveToNext();
      }
    }
    return count;
  }


  public HashMap<String, Integer> getNanoScore() {
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
          boolean isLiked = cursor.getInt(isLikedIdIndex)>0;
          if(user_name.equals(username) && isLiked){
            for(int i=0;i<nanoData.length();i++){
              String nanoString = nanoData.get(i).toString();
              if(scores.containsKey(nanoString)){
                int score = scores.get(nanoString)+1;
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
    }
    return scores;
  }


}
