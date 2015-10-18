package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.parse.ParseException;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;
import com.udacity.pathfinder.android.udacitypathfinder.ui.misc.DividerItemDecoration;

import java.util.List;

import timber.log.Timber;

public class FeedActivity extends AppCompatActivity {

  private RecyclerView recyclerView;
  private FeedAdapter feedAdapter;

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

    feedAdapter = new FeedAdapter(this);
    recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    recyclerView.setAdapter(feedAdapter);

    requestArticles();
  }

  private void requestArticles() {
    ParseClient.request(ParseConstants.ARTICLE_CLASS_NAME, true, new RequestCallback<Article>() {
      @Override public void onResponse(List<Article> articles, ParseException e) {
        if (e == null && articles != null && !articles.isEmpty()) {
          feedAdapter.updateArticles(articles);
        } else {
          Timber.e(e, "Error occurred while retrieving articles");
        }
      }
    });
  }
}
