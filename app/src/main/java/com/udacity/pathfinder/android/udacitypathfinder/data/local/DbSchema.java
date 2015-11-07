package com.udacity.pathfinder.android.udacitypathfinder.data.local;

import android.provider.BaseColumns;

interface DbSchema {

  // database schema
  String TABLE_ARTICLE_LIKES = "article_likes";
  String COLUMN_ID = BaseColumns._ID;
  String COLUMN_USERNAME = "username";
  String COLUMN_ARTICLE_ID = "article_id";
  String COLUMN_ARTICLE_IS_LIKED = "is_liked";
  String COLUMN_DATE_ADDED = "date_added";
  String COLUMN_ARTICLE_NANODEGREES = "article_nanodegrees";
}
