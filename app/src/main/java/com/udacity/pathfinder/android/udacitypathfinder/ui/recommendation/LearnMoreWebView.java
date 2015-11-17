package com.udacity.pathfinder.android.udacitypathfinder.ui.recommendation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.udacity.pathfinder.android.udacitypathfinder.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LearnMoreWebView extends AppCompatActivity {
  @Bind(R.id.toolbar)
  Toolbar toolbar;
  @Bind(R.id.spinner)
  ProgressBar spinner;

  private WebView webView;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fullscreen_webview);
    Intent i = getIntent();
    String url = i.getStringExtra("url");
    String title = i.getStringExtra("title");
    ButterKnife.bind(this);
    toolbar.setTitle(title);
    setSupportActionBar(toolbar);


    webView = (WebView) findViewById(R.id.webview);
    webView.getSettings().setJavaScriptEnabled(true);

    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(webView, url);
        spinner.setVisibility(View.INVISIBLE);
      }
    });

    webView.loadUrl(url);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_add_article, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_cancel) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    webView.removeAllViews();;
    webView.destroy();
  }
}
