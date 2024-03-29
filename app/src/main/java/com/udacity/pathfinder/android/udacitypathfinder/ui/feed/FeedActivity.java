package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.parse.ParseException;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.auth.AuthCompatActivity;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.DbArticleLikes;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;
import com.udacity.pathfinder.android.udacitypathfinder.ui.addArticle.AddArticleActivity;
import com.udacity.pathfinder.android.udacitypathfinder.ui.misc.DividerItemDecoration;
import com.udacity.pathfinder.android.udacitypathfinder.ui.recommendation.RecommendNanodegree;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class FeedActivity extends AuthCompatActivity {

  @BindString(R.string.title_activity_feed)
  String FEED_ACTIVITY_TITLE;
  @BindString(R.string.title_tab_grid)
  String GRID_TAB_TITLE;
  @BindString(R.string.title_tab_list)
  String LIST_TAB_TITLE;
  @BindString(R.string.title_tab_liked)
  String LIKED_TAB_TITLE;
  @Bind(R.id.toolbar)
  Toolbar toolbar;
  @Bind(R.id.recyclerview)
  RecyclerView recyclerView;
  @Bind(R.id.tabs)
  TabLayout tabLayout;
  @Bind(R.id.btn_recommendation)
  ImageButton btn_recomendation;
  SharedPref sp;
  boolean isLikedView;

  private FeedAdapter feedAdapter;
  private DividerItemDecoration dividerItemDecoration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
    ButterKnife.bind(this);
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    notificationManager.cancel(102);
    sp = new SharedPref(this);
    checkRecommendationStatus();

    toolbar.setTitle(FEED_ACTIVITY_TITLE);
    toolbar.setLogo(R.drawable.ic_app_compass);
    setSupportActionBar(toolbar);

    tabLayout.addTab(tabLayout.newTab().setText(GRID_TAB_TITLE));
    tabLayout.addTab(tabLayout.newTab().setText(LIST_TAB_TITLE));
    tabLayout.addTab(tabLayout.newTab().setText(LIKED_TAB_TITLE));
    tabLayout.setOnTabSelectedListener(onTabSelectedListener);

    dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);

    setupGridLayout();
  }

  private void checkRecommendationStatus() {
    if (!sp.isRecomended()) {
      btn_recomendation.setImageResource(R.mipmap.ic_paper_plane_0);
    } else {
      btn_recomendation.setImageResource(R.mipmap.ic_paper_plane_1);
      btn_recomendation.setAlpha((float) 1);
      btn_recomendation.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(FeedActivity.this, RecommendNanodegree.class);
          startActivity(i);
        }
      });
    }
  }

  private void setupGridLayout() {
    feedAdapter = new FeedAdapter(this, FeedAdapter.VIEW_TYPE_GRID);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        return (position % 3) > 0 ? 1 : 2;
      }
    });
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.removeItemDecoration(dividerItemDecoration);
    recyclerView.setAdapter(feedAdapter);
    recyclerView.setHasFixedSize(true);
    requestArticles();
    isLikedView = false;
  }

  private void setupListLayout() {
    feedAdapter = new FeedAdapter(this, FeedAdapter.VIEW_TYPE_LIST);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(dividerItemDecoration);
    recyclerView.setAdapter(feedAdapter);
    recyclerView.setHasFixedSize(true);
    requestArticles();
    isLikedView = false;
  }

  private void setupLikedLayout() {
    feedAdapter = new FeedAdapter(this, FeedAdapter.VIEW_TYPE_LIKED);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(feedAdapter);
    recyclerView.setHasFixedSize(true);
    requestLikedArticles();
    isLikedView = true;
  }

  private void requestArticles() {
    ParseClient.request(ParseConstants.ARTICLE_CLASS_NAME, true, new RequestCallback<Article>() {
      @Override
      public void onResponse(List<Article> articles, ParseException e) {
        if (e == null && articles != null) {
          if (!articles.isEmpty()) {
            feedAdapter.updateArticles(articles);
            if (!sp.isFirstFunComplete()) {
              Log.d("TEST", "-=-=-= Running first Run-=-=-=");
              new DbArticleLikes(getApplicationContext()).restoreUserArticleLikes();
            }
          }
        } else {
          Timber.e(e, "Error occurred while retrieving articles");
        }
      }
    });
  }

  private void requestLikedArticles() {
    ParseClient.request(ParseConstants.ARTICLE_CLASS_NAME, true, new RequestCallback<Article>() {
      @Override
      public void onResponse(List<Article> articles, ParseException e) {
        DbArticleLikes likeDb = new DbArticleLikes(getBaseContext());
        List<Article> likedArticles = new ArrayList<>();
        String articleId = " ";
        if (e == null && articles != null) {
          if (!articles.isEmpty()) {
            for (Article articleObject : articles) {
              articleId = articleObject.getObjectId();
              if (likeDb.alreadyLiked(articleId) && likeDb.isLiked(articleId)) {
                likedArticles.add(articleObject);
              }
            }
            feedAdapter.updateArticles(likedArticles);
          }
        } else {
          Timber.e(e, "Error occurred while retrieving articles");
        }
      }
    });
  }

  @OnClick(R.id.fab)
  public void addArticle(View view) {
    Intent intent = new Intent(this, AddArticleActivity.class);
    startActivity(intent);
  }

  private final TabLayout.OnTabSelectedListener onTabSelectedListener =
    new TabLayout.OnTabSelectedListener() {

      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
          case 0:
            setupGridLayout();
            break;
          case 1:
            setupListLayout();
            break;
          case 2:
            setupLikedLayout();
            break;
          default:
            break;
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    };

  @Override
  protected void onResume() {
    super.onResume();
    checkRecommendationStatus();
    if (isLikedView) setupLikedLayout();
  }


}
