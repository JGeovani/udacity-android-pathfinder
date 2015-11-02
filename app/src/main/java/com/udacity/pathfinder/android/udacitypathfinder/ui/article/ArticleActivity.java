package com.udacity.pathfinder.android.udacitypathfinder.ui.article;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
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


  @BindString(R.string.title_activity_article)
  String ARTICLE_ACTIVITY_TITLE;
  @Bind(R.id.toolbar)
  Toolbar toolbar;
  @Bind(R.id.webview)
  WebView webView;
  @Bind(R.id.spinner)
  ProgressBar spinner;
  @Bind(R.id.banner)
  TextView banner;
  @Bind(R.id.btn_exit)
  ImageButton btn_exit;
  @Bind(R.id.btn_like)
  ImageButton btn_like;

  public static final String KEY_ARTICLE_OBJECT_ID = "articleObjectId";
  public static final String KEY_ARTICLE_NANODEGREES = "nanodegrees";
  private String articleId;
  private ArrayList<String> arraylist;
  String[] nanodegrees;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    likeDb = new DbArticleLikes(this);
    sp = new SharedPref(this);
    setContentView(R.layout.activity_article);
    ButterKnife.bind(this);
    btn_like.setOnClickListener(this);
    btn_exit.setOnClickListener(this);

    toolbar.setTitle(ARTICLE_ACTIVITY_TITLE);
    setSupportActionBar(toolbar);

    banner.setVisibility(View.INVISIBLE);
    spinner.setVisibility(View.VISIBLE);

    webView.setWebViewClient(webViewClient);

    Intent intent = getIntent();
    articleId = intent.getStringExtra(KEY_ARTICLE_OBJECT_ID);
    requestArticle();
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
            btn_like.setImageResource(R.drawable.heart_icon_1);
          }
          List<String> nandegreeData = article.getNanodegrees();
          for(int i=0;i<nandegreeData.size();i++){
            arraylist.add(nandegreeData.get(i));
          }
          Log.d("total: ", String.valueOf(arraylist.size()));
          sp.saveNanodegree(arraylist);

          // start webview
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

    @Override
    public void onPageFinished(WebView view, String url) {
      spinner.setVisibility(View.INVISIBLE);
      banner.setVisibility(View.VISIBLE);
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
      btn_like.setImageResource(R.drawable.heart_icon_1);
      likeDb.addLike(articleId,nano);
      this.isLiked = true;
    } else if (!isLiked && likeDb.alreadyLiked(articleId)) {
      likeDb.updateLike(articleId, true);
      btn_like.setImageResource(R.drawable.heart_icon_1);
      this.isLiked = true;
    } else if (isLiked) {
      btn_like.setImageResource(R.drawable.heart_icon_0);
      likeDb.updateLike(articleId, false);
      this.isLiked = false;
    }

  }

}
