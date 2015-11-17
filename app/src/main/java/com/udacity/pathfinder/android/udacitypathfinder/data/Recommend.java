package com.udacity.pathfinder.android.udacitypathfinder.data;


import android.content.Context;
import android.util.Log;

import com.udacity.pathfinder.android.udacitypathfinder.data.local.DbArticleLikes;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Recommend {

  Context context;
  DbArticleLikes db;
  HashMap<String, Integer> nanoMap;

  public Recommend(Context ctx){
    context = ctx;
    db = new DbArticleLikes(context);
    nanoMap = db.getNanoScore();
  }

  // return top 3 scores in order of highest score value
  public String[][] nanodegree() {

    String[][] recommendedDegrees = new String[3][2];
    int count = 0;
    Object[] mapSet = nanoMap.entrySet().toArray();
    Arrays.sort(mapSet, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
          ((Map.Entry<String, Integer>) o1).getValue());
      }
    });
    for (Object data : mapSet) {
      String degree = ((Map.Entry<String, Integer>) data).getKey();
      String score = String.valueOf(((Map.Entry<String, Integer>) data).getValue());
      if (count <3){
        recommendedDegrees[count][0] = degree;
        recommendedDegrees[count][1] = score;
        if(count == 0){
          new SharedPref(context).setRecommendationTopScore(((Map.Entry<String, Integer>) data).getValue());
        }
        Log.d("Recommendation # "+(count+1)+" = ", (degree + " : Total Likes = "+ score));
        count++;
      }
    }
    return recommendedDegrees;
  }

  // check to see if recommendation is ready
  public boolean isReady() {
    boolean isRecommendationReady = false;
    String[][] nanodegreeTopThree = nanodegree();
    if (nanodegreeTopThree[0][1]!=null) {
      int topScore = Integer.parseInt(nanodegreeTopThree[0][1]);
      if (topScore > 4) isRecommendationReady = true;
    }
    return isRecommendationReady;
  }
}
