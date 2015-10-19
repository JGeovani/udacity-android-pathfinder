package com.udacity.pathfinder.android.udacitypathfinder.data;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

public interface RequestCallback<T extends ParseObject> {
  void onResponse(List<T> objects, ParseException e);
}
