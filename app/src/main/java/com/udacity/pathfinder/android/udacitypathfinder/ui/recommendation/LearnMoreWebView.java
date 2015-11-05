package com.udacity.pathfinder.android.udacitypathfinder.ui.recommendation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
  @Bind(R.id.btn_exit)
  ImageButton btn_exit;
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
    btn_exit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });


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
  protected void onDestroy() {
    super.onDestroy();
    webView.removeAllViews();;
    webView.destroy();
  }
}
