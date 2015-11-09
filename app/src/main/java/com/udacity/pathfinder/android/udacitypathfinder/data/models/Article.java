package com.udacity.pathfinder.android.udacitypathfinder.data.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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

  public String getDomain() {
    try {
      URI uri = new URI(getString("link"));
      String domain = uri.getHost();
      return domain.startsWith("www.") ? domain.substring(4) : domain;
    } catch (URISyntaxException ex) {
      return "..";
    }

  }

  public List<String> getNanodegrees() {
    return getList("nanodegrees");
  }

  public String getTitle() {
    return getString("title");
  }

  public String getId() {
    return getString("id");
  }
}