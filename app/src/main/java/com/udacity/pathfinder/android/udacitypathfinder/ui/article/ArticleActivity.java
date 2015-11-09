package com.udacity.pathfinder.android.udacitypathfinder.ui.article;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseException;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.Recommend;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback2;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.DbArticleLikes;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Nanodegree;
import com.udacity.pathfinder.android.udacitypathfinder.ui.recommendation.LearnMoreWebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ArticleActivity extends AppCompatActivity implements View.OnClickListener {
  private final String TAG = getClass().getSimpleName();
  private SharedPref sp;
  private boolean isLiked = false;
  DbArticleLikes likeDb;
  Recommend recommend;
  HashMap<String, String> nanoMap = new HashMap<>();

  @BindString(R.string.title_activity_article)
  String ARTICLE_ACTIVITY_TITLE;
  @Bind(R.id.toolbar)
  Toolbar toolbar;
  @Bind(R.id.webview)
  WebView webView;
  @Bind(R.id.spinner)
  ProgressBar spinner;
  @Bind(R.id.tv_banner)
  TextView tv_banner;
  @Bind(R.id.btn_exit)
  ImageButton btn_exit;
  @Bind(R.id.btn_like)
  ImageButton btn_like;
  @Bind(R.id.tv_title)
  TextView tv_nanodegree_title;
  @Bind(R.id.tv_learn_more)
  TextView tv_nanodegree_learn_more;
  @Bind(R.id.iv_nanodegree)
  ImageView iv_nanodegree;
  @Bind(R.id.fl_nanodegree)
  FrameLayout fl_nanodegree;


  public static final String KEY_ARTICLE_OBJECT_ID = "articleObjectId";
  private String articleId, degreeUrl, degreeTitle;
  private ArrayList<String> arraylist;
  private ArrayList<Nanodegree> nanoObjects;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Obtain and update nanoMap with nanodegree data
    getNanodegreeData();
    likeDb = new DbArticleLikes(this);
    recommend = new Recommend(this);
    sp = new SharedPref(this);
    setContentView(R.layout.activity_article);
    ButterKnife.bind(this);
    isWebLoadComplete(false);
    btn_like.setOnClickListener(this);
    btn_exit.setOnClickListener(this);
    toolbar.setTitle(ARTICLE_ACTIVITY_TITLE);
    toolbar.setSubtitle("Subtitle");
    toolbar.setLogo(R.drawable.ic_app_compass);
    setSupportActionBar(toolbar);
    Intent intent = getIntent();
    articleId = intent.getStringExtra(KEY_ARTICLE_OBJECT_ID);
    requestArticle();
  }

  private void getNanodegreeData() {
    nanoObjects = new ArrayList<>();
    ParseClient.request(ParseConstants.NANODEGREE_CLASS_NAME, true, new RequestCallback<Nanodegree>() {
      @Override
      public void onResponse(List<Nanodegree> nanodegree, ParseException e) {
        if (e == null && nanodegree != null) {
          if (!nanodegree.isEmpty()) {
            for (int i = 0; i < nanodegree.size(); i++) {
              nanoObjects.add(nanodegree.get(i));
            }
          } else {
            Timber.e(e, "Error occurred while retrieving nanodegree data");
          }
        }
      }
    });
  }

  private void isWebLoadComplete(boolean visable) {
    if (visable) {
      spinner.setVisibility(View.INVISIBLE);
      fl_nanodegree.setVisibility(View.VISIBLE);
      Log.d(TAG, "WebView loaded, now suggesting " + tv_nanodegree_title.getText());
    } else {
      spinner.setVisibility(View.VISIBLE);
      fl_nanodegree.setVisibility(View.GONE);
      Log.d(TAG, "WebView is now loading");
    }
  }

  private void requestArticle() {
    ParseClient.request(
      ParseConstants.ARTICLE_CLASS_NAME, true, articleId, new RequestCallback2<Article>() {
        @Override
        public void onResponse(Article article, ParseException e) {
          // Check if article is already liked
          arraylist = new ArrayList<>();
          if (likeDb.alreadyLiked(articleId)) {
            isLiked = true;
            btn_like.setImageResource(R.mipmap.ic_heart_1);
          }
          List<String> nandegreeData = article.getNanodegrees();
          for (int i = 0; i < nandegreeData.size(); i++) {
            arraylist.add(nandegreeData.get(i));
          }
          sp.saveNanodegree(arraylist);
          // We need to set DomStorage to enabled to prevent url from escaping webview and causing
          //  "Cannot call determinedVisibility()" with BinderManager
          webView.getSettings().setDomStorageEnabled(true);
          webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
              // we want all views to stay within webView
              view.loadUrl(url);
              return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
              setNanodegreeAsset(arraylist.get(0));
              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                public void run() {
                  isWebLoadComplete(true);
                }
              }, 750);
            }
          });
          if(webView!=null)
            toolbar.setTitle(getSafeTitle(capitalizeString(article.getTitle())));
            toolbar.setSubtitle(article.getDomain());
             webView.loadUrl(article.getLink());
        }
      });
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_like:
        setLike(isLiked);
        break;
      case R.id.btn_exit:
        finish();
        break;
    }
  }

  private void setLike(boolean isLiked) {
    String[] nano = sp.getNanodegrees();
    if (!isLiked && !likeDb.alreadyLiked(articleId)) {
      btn_like.setImageResource(R.mipmap.ic_heart_1);
      likeDb.addLike(articleId, nano);
      this.isLiked = true;
    } else if (!isLiked && likeDb.alreadyLiked(articleId)) {
      likeDb.updateLike(articleId, true);
      btn_like.setImageResource(R.mipmap.ic_heart_1);
      this.isLiked = true;
    } else if (isLiked) {
      btn_like.setImageResource(R.mipmap.ic_heart_0);
      likeDb.updateLike(articleId, false);
      this.isLiked = false;
    }
    // Checking if recommendation is ready
    if (recommend.isReady()) {
      sp.saveRecomendation(true);
    } else {
      sp.saveRecomendation(false);
    }
  }

  private void setNanodegreeAsset(final String nanodegree) {
    for (final Nanodegree object : nanoObjects) {
      if (object.getDegreeId().equals(nanodegree)) {
        if (object.getDegreeId().equals(nanodegree)) {
          degreeUrl = object.getDegreeUrl();
          degreeTitle = capitalizeString(object.getDegreeTitle());
          tv_nanodegree_title.setText(degreeTitle);
          tv_banner.setText(R.string.introducing);
          tv_nanodegree_learn_more.setText("Learn More");
          tv_nanodegree_learn_more.setOnTouchListener(
            new View.OnTouchListener() {
              @Override
              public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                  case MotionEvent.ACTION_DOWN:
                    Intent i = new Intent(getBaseContext(), LearnMoreWebView.class);
                    i.putExtra("url", degreeUrl);
                    i.putExtra("title", degreeTitle);
                    startActivity(i);
                    break;
                }
                return true;
              }
            }
          );
          Glide.with(this)
            .load(object.getBannerImage())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(iv_nanodegree);
          /**
           * If desired we can load from app image resources,
           * yet this will take away dynamic loading from url
           *
           * 1: int imageResource = getResources().getIdentifier(nanodegree, "drawable", getPackageName());
           * 2: iv_nanodegree.setImageResource(imageResource);
           */
        } else {
          // there is no degree info, hiding footer for now
          fl_nanodegree.setVisibility(View.GONE);
        }
      }
    }
  }

  public static String capitalizeString(final String data) {
    char[] chars = data.toLowerCase().toCharArray();
    boolean found = false;
    for (int i = 0; i < chars.length; i++) {
      if (!found && Character.isLetter(chars[i])) {
        chars[i] = Character.toUpperCase(chars[i]);
        found = true;
      } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
        found = false;
      }
    }
    return String.valueOf(chars);
  }

  public String getSafeTitle(String data){
    int length = 18;
    if(!TextUtils.isEmpty(data)){
      if(data.length() >= length){
        return data.substring(0, length)+"...";
      }
    }
    return data;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    webView.removeAllViews();
    webView.destroy();
  }
}
