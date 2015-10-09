package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.parse.Article;

import java.util.List;

public class FeedActivity extends AppCompatActivity {

  private static final String TAG = "FeedActivity";
  private static final String ARTICLES_LABEL = "Articles";
  private static final String CHECKED_CLOUD_KEY = "checkedCloud";

  private RecyclerView recyclerView;
  private FeedAdapter feedAdapter;
  private boolean checkedCloud;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
    Resources res = getResources();

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(res.getString(R.string.title_activity_feed));
    setSupportActionBar(toolbar);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.addTab(tabLayout.newTab().setText(res.getString(R.string.title_tab_grid)));
    tabLayout.addTab(tabLayout.newTab().setText(res.getString(R.string.title_tab_list)));

    feedAdapter = new FeedAdapter();
    recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(feedAdapter);

    ParseQuery<Article> query = ParseQuery.getQuery(ARTICLES_LABEL);
    query.fromLocalDatastore();
    query.findInBackground(callback);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(CHECKED_CLOUD_KEY, checkedCloud);
    super.onSaveInstanceState(outState);
  }

  @Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    checkedCloud = savedInstanceState.getBoolean(CHECKED_CLOUD_KEY);
  }

  private final FindCallback<Article> callback = new FindCallback<Article>() {
    @Override public void done(List<Article> list, ParseException e) {
      if (e == null) {
        if (list.isEmpty() && !checkedCloud) {
          Log.d(TAG, "Local datastore is empty, checking the cloud...");
          ParseQuery<Article> query = ParseQuery.getQuery(ARTICLES_LABEL);
          query.findInBackground(new FindCallback<Article>() {
            @Override public void done(final List<Article> list, ParseException e) {
              checkedCloud = true;
              if (e == null) {
                Log.d(TAG, list.size() + " results found in the cloud");
                if (!list.isEmpty()) {
                  ParseObject.pinAllInBackground(ARTICLES_LABEL, list);
                  feedAdapter.updateArticles(list);
                }
              } else {
                Log.e(TAG, "Error occurred while attempting to retrieve data from the cloud", e);
              }
            }
          });
        } else if (!list.isEmpty()) {
          Log.d(TAG, list.size() + " results found in local datastore");
          feedAdapter.updateArticles(list);
        } else {
          Log.d(TAG, "Local datastore empty. Cloud is empty as well or simply could not connect");
        }
      } else {
        Log.e(TAG, "Error occurred while attempting to retreive data from local datastore", e);
      }
    }
  };
}
