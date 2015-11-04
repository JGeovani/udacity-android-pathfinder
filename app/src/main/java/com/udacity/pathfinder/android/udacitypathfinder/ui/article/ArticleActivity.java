package com.udacity.pathfinder.android.udacitypathfinder.ui.article;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.Recommend;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback2;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.DbArticleLikes;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity implements View.OnClickListener {
  private final String TAG = getClass().getSimpleName();
  private SharedPref sp;
  private boolean isLiked = false;
  DbArticleLikes likeDb;
  Recommend recommend;


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
  private String articleId;
  private ArrayList<String> arraylist;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    likeDb = new DbArticleLikes(this);
    recommend = new Recommend(this);
    sp = new SharedPref(this);
    setContentView(R.layout.activity_article);
    ButterKnife.bind(this);
    btn_like.setOnClickListener(this);
    btn_exit.setOnClickListener(this);
    toolbar.setTitle(ARTICLE_ACTIVITY_TITLE);
    setSupportActionBar(toolbar);
    isWebLoadComplete(false);
    webView.setWebViewClient(webViewClient);
    Intent intent = getIntent();
    articleId = intent.getStringExtra(KEY_ARTICLE_OBJECT_ID);
    requestArticle();
  }

  private void isWebLoadComplete(boolean visable) {
    if (visable) {
      spinner.setVisibility(View.INVISIBLE);
      fl_nanodegree.setVisibility(View.VISIBLE);
      Log.d(TAG, "WebView loaded, now suggesting " + tv_nanodegree_title.getText());
    } else {
      spinner.setVisibility(View.VISIBLE);
      fl_nanodegree.setVisibility(View.INVISIBLE);
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
          webView.setWebViewClient(new WebViewClient() {
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
          webView.loadUrl(article.getLink());
        }
      });
  }

  private final WebViewClient webViewClient = new WebViewClient() {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      // Launch any links inside the web view via a browser, not inside the web view itself
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      view.getContext().startActivity(intent);
      return true;
    }

  };

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
    // getting the count of likes for end user
    if (likeDb.totalCount() > 4) {
      Log.d("-=- ", "Total of " + String.valueOf(likeDb.totalCount()) + "likes -=-");
      sp.saveRecomendation(true);
    } else {
      sp.saveRecomendation(false);
    }
  }

  private void setNanodegreeAsset(String nanodegree) {
    /**
     * intro to programming = nd000
     * web developer = ca001
     * front end web developer = nd001
     * data analyst = nd002
     * ios = nd003
     * full stack web = nd004
     * beginning ios = nd006
     * tech entrepreneur = nd007
     * android = nd801
     */
    String learnMore = "learn_more_" + nanodegree;
    int learnMoreResource = getResources().getIdentifier(learnMore, "string", getPackageName());
    int imageResource = getResources().getIdentifier(nanodegree, "drawable", getPackageName());
    String title = "";

    switch (nanodegree) {

      case "nd000":
        title = "Intro to Programming";
        break;

      case "ca001":
        title = "Web Developer";
        break;

      case "nd001":
        title = "Front-End Web Developer";
        break;

      case "nd002":
        title = "Data Analyst";
        break;

      case "nd003":
        title = "iOS Developer";
        break;

      case "nd004":
        title = "Full Stack Web Developer";
        break;

      case "nd006":
        title = "Beginning iOS Developer";
        break;

      case "nd007":
        title = "Tech Entrepreneur";
        break;

      case "nd801":
        title = "Android Developer";
        break;

    }
    tv_banner.setText("INTRODUCING");
    tv_nanodegree_title.setText(title + " Nanodegree");
    tv_nanodegree_learn_more.setText(learnMoreResource);
    tv_nanodegree_learn_more.setMovementMethod(LinkMovementMethod.getInstance());
    iv_nanodegree.setImageResource(imageResource);
  }

}
