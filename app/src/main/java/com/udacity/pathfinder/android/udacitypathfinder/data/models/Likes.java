package com.udacity.pathfinder.android.udacitypathfinder.data.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Likes")
public class Likes extends ParseObject {

  public Likes() {
    super();
  }

  public String getArticleId() {
    return getString("article_id");
  }

  public Boolean getIsLiked() {
    return getBoolean("is_liked");
  }

}