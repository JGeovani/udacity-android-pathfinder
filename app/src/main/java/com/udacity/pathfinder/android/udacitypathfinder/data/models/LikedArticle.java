package com.udacity.pathfinder.android.udacitypathfinder.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LikedArticle implements Parcelable {
  String articleId;
  boolean isLiked;

  public String getArticleId() {
    return articleId;
  }

  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }

  public boolean isLiked() {
    return isLiked;
  }

  public void setIsLiked(boolean isLiked) {
    this.isLiked = isLiked;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.articleId);
    dest.writeByte(isLiked ? (byte) 1 : (byte) 0);
  }

  public LikedArticle() {
  }

  protected LikedArticle(Parcel in) {
    this.articleId = in.readString();
    this.isLiked = in.readByte() != 0;
  }

  public static final Parcelable.Creator<LikedArticle> CREATOR = new Parcelable.Creator<LikedArticle>() {
    public LikedArticle createFromParcel(Parcel source) {
      return new LikedArticle(source);
    }

    public LikedArticle[] newArray(int size) {
      return new LikedArticle[size];
    }
  };
}
