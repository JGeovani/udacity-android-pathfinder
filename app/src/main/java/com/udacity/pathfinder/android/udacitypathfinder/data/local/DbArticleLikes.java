package com.udacity.pathfinder.android.udacitypathfinder.data.local;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    String whereClause = dbHelper.COLUMN_ARTICLE_ID + " = ? AND " + dbHelper.COLUMN_USERNAME + " = ?";
    String[] whereArgs = {articleId, username};
    db.delete(DbHelper.TABLE_ARTICLE_LIKES, whereClause, whereArgs);
    Log.d(TAG, "User: " + username + ", deleted article " + articleId);
  }

  public void updateLike(String articleId, boolean isLiked) {
    ContentValues cv = new ContentValues();
    cv.put(dbHelper.COLUMN_ARTICLE_IS_LIKED, isLiked);
    String username = sp.getUserId();
    String whereClause = dbHelper.COLUMN_ARTICLE_ID + " = ? AND " + dbHelper.COLUMN_USERNAME + " = ?";
    String[] whereArgs = {articleId, username};
    db.update(DbHelper.TABLE_ARTICLE_LIKES, cv, whereClause, whereArgs);
    Log.d(TAG, "User: " + username + ", updated article " + articleId + " liked: " + isLiked);
  }
}
