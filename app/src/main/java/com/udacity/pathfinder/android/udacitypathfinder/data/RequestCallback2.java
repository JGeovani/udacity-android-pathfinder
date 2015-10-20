package com.udacity.pathfinder.android.udacitypathfinder.data;

import com.parse.ParseException;
import com.parse.ParseObject;

public interface RequestCallback2<T extends ParseObject> {
  void onResponse(T object, ParseException e);
}
