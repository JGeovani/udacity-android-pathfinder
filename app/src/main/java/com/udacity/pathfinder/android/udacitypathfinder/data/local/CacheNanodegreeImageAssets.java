package com.udacity.pathfinder.android.udacitypathfinder.data.local;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.parse.ParseException;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Nanodegree;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class CacheNanodegreeImageAssets implements Runnable {

  Context context;
  public CacheNanodegreeImageAssets(Context ctx){
    context = ctx;
  }

  /**
   * Cache image data using Glide
   * https://github.com/bumptech/glide
   */
  @Override
  public void run () {
    ParseClient.request(ParseConstants.NANODEGREE_CLASS_NAME, true, new RequestCallback<Nanodegree>() {
      @Override
      public void onResponse(List<Nanodegree> nanodegree, ParseException e) {
        if (e == null && nanodegree != null) {
          if (!nanodegree.isEmpty()) {
            for (int i = 0; i < nanodegree.size(); i++) {
              final Nanodegree degreeObject = nanodegree.get(i);
              final String imageUrl = degreeObject.getImage();
              final String bannerImageUrl = degreeObject.getBannerImage();
              Runnable getImage = new Runnable() {
                @Override
                public void run() {
                  FutureTarget<File> future = Glide.with(context)
                    .load(imageUrl)
                    .downloadOnly(800, 492);
                  FutureTarget<File> future2 = Glide.with(context)
                    .load(bannerImageUrl)
                    .downloadOnly(800, 165);
                  try {
                    File cacheFile = future.get();
                    File cacheFile2 = future2.get();
                    Log.d("Cached image ", ": " + imageUrl);
                    Log.d("Cached banner image ", ": " + bannerImageUrl);
                  } catch (InterruptedException e1) {
                    e1.printStackTrace();
                  } catch (ExecutionException e1) {
                    e1.printStackTrace();
                  }
                }
              };
              new Thread(getImage).start();
            }
          }
        } else {
          Timber.e(e, "Error occurred while retrieving nanodegree image cache data");
        }
      }
    });
  }
}