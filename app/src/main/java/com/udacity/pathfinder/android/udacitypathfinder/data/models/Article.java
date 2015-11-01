package com.udacity.pathfinder.android.udacitypathfinder.data.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Articles")
public class Article extends ParseObject {

  public Article() {
    super();
  }

  public String getCategory() {
    return getString("category");
  }

  public String getDescription() {
    return getString("description");
  }

  public String getImageUrl() {
    return getString("imageUrl");
  }

  public String getLink() {
    return getString("link");
  }

  public String getNanodegree() {
    return getString("nanodegree");
  }

  public String getTitle() {
    return getString("title");
  }

  public String getId() {
    return getString("id");
  }
}