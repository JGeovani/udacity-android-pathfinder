package com.udacity.pathfinder.android.udacitypathfinder.data.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Nanodegree")
public class Nanodegree extends ParseObject {

  public Nanodegree() {
    super();
  }

  public String getDegreeId() {
    return getString("degree_id");
  }

  public String getBannerImage() {
    return getString("banner_image");
  }

  public Boolean getAvailable() {
    return getBoolean("available");
  }

  public String getDegreeTitle() {
    return getString("degree_title");
  }

  public String getDegreeUrl() {
    return getString("degree_url");
  }

  public String getShortSummary() {
    return getString("short_summary");
  }

  public String getImage() {
    return getString("image");
  }

  public List<String> getTracks(){
    return getList("tracks");
  }
}