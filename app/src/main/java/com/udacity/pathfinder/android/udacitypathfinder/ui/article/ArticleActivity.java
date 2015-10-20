package com.udacity.pathfinder.android.udacitypathfinder.ui.article;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback2;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {

  @BindString(R.string.title_activity_article) String ARTICLE_ACTIVITY_TITLE;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.webview) WebView webView;
  @Bind(R.id.spinner) ProgressBar spinner;
  @Bind(R.id.banner) TextView banner;

  public static final String KEY_ARTICLE_OBJECT_ID = "articleObjectId";
  private String articleId;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article);
    ButterKnife.bind(this);

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
      @Override public void onResponse(Article article, ParseException e) {
        webView.loadUrl(article.getLink());
      }
    });
  }

  private final WebViewClient webViewClient = new WebViewClient() {

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
      // Launch any links inside the web view via a browser, not inside the web view itself
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      view.getContext().startActivity(intent);
      return true;
    }

    @Override public void onPageFinished(WebView view, String url) {
      spinner.setVisibility(View.INVISIBLE);
      banner.setVisibility(View.VISIBLE);
    }
  };
}
