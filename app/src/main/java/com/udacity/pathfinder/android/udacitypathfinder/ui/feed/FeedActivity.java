package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FeedActivity extends AppCompatActivity {

  @BindString(R.string.title_activity_feed) String FEED_ACTIVITY_TITLE;
  @BindString(R.string.title_tab_grid) String GRID_TAB_TITLE;
  @BindString(R.string.title_tab_list) String LIST_TAB_TITLE;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.recyclerview) RecyclerView recyclerView;
  @Bind(R.id.tabs) TabLayout tabLayout;

  private FeedAdapter feedAdapter;
  private DividerItemDecoration dividerItemDecoration;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
    ButterKnife.bind(this);

    toolbar.setTitle(FEED_ACTIVITY_TITLE);
    setSupportActionBar(toolbar);

    tabLayout.addTab(tabLayout.newTab().setText(GRID_TAB_TITLE));
    tabLayout.addTab(tabLayout.newTab().setText(LIST_TAB_TITLE));
    tabLayout.setOnTabSelectedListener(onTabSelectedListener);

    dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);

    setupGridLayout();
    requestArticles();
  }

  private void setupGridLayout() {
    feedAdapter = new FeedAdapter(this, FeedAdapter.VIEW_TYPE_GRID);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        return (position % 3) > 0 ? 1 : 2;
      }
    });
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.removeItemDecoration(dividerItemDecoration);
    recyclerView.setAdapter(feedAdapter);
  }

  private void setupListLayout() {
    feedAdapter = new FeedAdapter(this, FeedAdapter.VIEW_TYPE_LIST);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(dividerItemDecoration);
    recyclerView.setAdapter(feedAdapter);
  }

  private void requestArticles() {
    ParseClient.request(ParseConstants.ARTICLE_CLASS_NAME, true, new RequestCallback<Article>() {
      @Override public void onResponse(List<Article> articles, ParseException e) {
        if (e == null && articles != null) {
          if (!articles.isEmpty()) feedAdapter.updateArticles(articles);
        } else {
          Timber.e(e, "Error occurred while retrieving articles");
        }
      }
    });
  }

  private final TabLayout.OnTabSelectedListener onTabSelectedListener =
      new TabLayout.OnTabSelectedListener() {

    @Override public void onTabSelected(TabLayout.Tab tab) {
      if (tab.getPosition() == 0) setupGridLayout();
      else setupListLayout();
      requestArticles();
    }

    @Override public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override public void onTabReselected(TabLayout.Tab tab) {
    }
  };
}
