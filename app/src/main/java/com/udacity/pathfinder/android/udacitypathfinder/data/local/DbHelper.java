package com.udacity.pathfinder.android.udacitypathfinder.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper implements DbSchema {

  private static final String TAG = DbHelper.class.getSimpleName();
  public static final String DB_NAME = "pathfinder.db";
  public static final int DB_VERSION = 1;

  public DbHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Configure Article Likes Table
    String sql_article_likes = String.format("create table %s ( %s INTEGER PRIMARY KEY,"
        + "%s TEXT,"
        + "%s BOOLEAN,"
        + "%s TEXT,"
        + "%s TEXT,"
        + "%s TEXT "
        + ");",
      TABLE_ARTICLE_LIKES,
      COLUMN_ID,
      COLUMN_USERNAME,
      COLUMN_ARTICLE_IS_LIKED,
      COLUMN_ARTICLE_ID,
      COLUMN_DATE_ADDED,
      COLUMN_ARTICLE_NANODEGREES);
    Log.d("DbHelper", "sql: " + sql_article_likes);
    db.execSQL(sql_article_likes);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
