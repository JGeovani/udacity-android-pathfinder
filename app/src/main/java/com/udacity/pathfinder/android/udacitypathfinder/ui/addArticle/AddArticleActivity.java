package com.udacity.pathfinder.android.udacitypathfinder.ui.addArticle;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.local.SharedPref;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class AddArticleActivity extends AppCompatActivity implements FetchImageTask.ImageResponse {

  @BindString(R.string.title_activity_add_article) String ADD_ARTICLE_ACTIVITY_TITLE;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.article_title) EditText titleEditText;
  @Bind(R.id.article_url) EditText articleUrlEditText;
  @Bind(R.id.image_url) EditText imageUrlEditText;
  @Bind(R.id.nanodegrees) TextView nanodegreesTextView;
  @Bind(R.id.btn_check_article) Button checkArticleButton;

  RecyclerView.LayoutManager mLayoutManager;
  CrawledGridAdapter mAdapter;
  RecyclerView mRecyclerView;

  ArrayList<String> imageItems = new ArrayList<>();

  @State Integer[] selectionIndices = new Integer[]{};
  @State String selectionsText;
  @State
  Integer[] selectionIndices = new Integer[]{};
  @State
  String selectionsText;

  private static final String DELIMITER = ", ";
  private MaterialDialog nanodegreesDialog;
  private MaterialDialog imageSelectDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_article);
    Icepick.restoreInstanceState(this, savedInstanceState);
    ButterKnife.bind(this);
    imageSelectDialog = getImageDialog();
    setupGrid();
    toolbar.setTitle(ADD_ARTICLE_ACTIVITY_TITLE);
    setSupportActionBar(toolbar);

    if (selectionsText != null) nanodegreesTextView.setText(selectionsText);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_add_article, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_cancel) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Icepick.saveInstanceState(this, outState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (nanodegreesDialog != null && nanodegreesDialog.isShowing()) {
      nanodegreesDialog.dismiss();
    }
  }

  @OnClick(R.id.nanodegrees)
  public void showNanodegreesDialog(View view) {
    nanodegreesDialog = new MaterialDialog.Builder(this)
        .title(R.string.dialog_nanodegrees_title)
        .items(R.array.nanodegree_titles)
        .itemsCallbackMultiChoice(selectionIndices, new MaterialDialog.ListCallbackMultiChoice() {
          @Override public boolean onSelection(MaterialDialog materialDialog, Integer[] integers,
              CharSequence[] values) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0, n = values.length; i < n; i++) {
              builder.append(values[i]);
              if (!(i == n - 1)) builder.append(DELIMITER);
            }
            selectionsText = builder.toString();
            nanodegreesTextView.setText(selectionsText);
            selectionIndices = integers;
            return true;
          }
        })
        .positiveText(R.string.dialog_nanodegrees_positive_text)
        .negativeText(R.string.dialog_nanodegrees_negative_text)
        .show();
  }

  @OnClick(R.id.btn_check_article)
  public void showImageSelectDialog(View view) {
    imageSelectDialog.show();
    final String articleUrl = articleUrlEditText.getText().toString();
    try {
      FetchImageTask imageTask = (FetchImageTask) new FetchImageTask(
              new FetchImageTask.ImageResponse() {
        @Override
        public void finishImageResponse(Document doc) {
          // Parse and assign article title.
          String articleTitle = doc.title();
          titleEditText.setText(articleTitle);
          Elements articleImages = doc.select("img[src]");

          int counter = 0;
          for (Element img : articleImages) {
            CrawledImageItem item = new CrawledImageItem();
            item.setName(img.attr("alt"));
            item.setThumbnail(img.absUrl("src"));
            mAdapter.add(counter, item);
            mAdapter.notifyItemInserted(counter);
            counter++;
          }
          mAdapter.notifyDataSetChanged();
        }
      }).execute(articleUrl);

    } catch (IllegalArgumentException mu) {
      Toast.makeText(this, "Invalid URL - " + articleUrl, Toast.LENGTH_SHORT).show();
    } catch (Exception ex) {
      Toast.makeText(this, "Invalid EXP - " + articleUrl, Toast.LENGTH_SHORT).show();
    }
  }

  @OnClick(R.id.fab)
  public void save(View view) {
    final String title = titleEditText.getText().toString();
    final String articleUrl = articleUrlEditText.getText().toString();
    final String imageUrl = imageUrlEditText.getText().toString();

    Pair<Boolean, String> validation = validateForm(title, articleUrl, imageUrl, selectionIndices);
    boolean validated = validation.first;

    if (validated) {
      String[] nanodegreeIds = getResources().getStringArray(R.array.nanodegree_ids);
      ArrayList<String> nanodegrees = new ArrayList<>();
      for (int i = 0, n = selectionIndices.length; i < n; i++) {
        nanodegrees.add(nanodegreeIds[selectionIndices[i]]);
      }

      SharedPref sp = new SharedPref(this);
      Article article = new Article();
      article.put(ParseConstants.ARTICLES_COL_TITLE, title);
      article.put(ParseConstants.ARTICLES_COL_LINK, articleUrl);
      article.put(ParseConstants.ARTICLES_COL_IMAGE_URL, imageUrl);
      article.put(ParseConstants.ARTICLES_COL_NANODEGREES, nanodegrees);
      article.put(ParseConstants.ARTICLES_COL_LIKES, 0);
      article.put(ParseConstants.ARTICLES_COL_APPROVED, false);
      article.put(ParseConstants.ARTICLES_COL_SUBMITTED_BY, sp.getUserId());
      article.saveInBackground();
      finish();
    } else {
      Snackbar.make(view, validation.second, Snackbar.LENGTH_LONG).show();
    }
  }

  private Pair<Boolean, String> validateForm(String title, String articleUrl, String imageUrl,
      Integer[] indices) {
    Resources res = getResources();
    if (title == null || title.isEmpty()) {
      return new Pair<>(false, res.getString(R.string.validation_enter_title));
    } else if (articleUrl == null || articleUrl.isEmpty() ||
        !Patterns.WEB_URL.matcher(articleUrl).matches()) {
      return new Pair<>(false, res.getString(R.string.validation_invalid_article_url));
    } else if (imageUrl != null && !imageUrl.isEmpty() &&
        !Patterns.WEB_URL.matcher(imageUrl).matches()) {
      return new Pair<>(false, res.getString(R.string.validation_invalid_image_url));
    } else if (indices == null || indices.length == 0) {
      return new Pair<>(false, res.getString(R.string.validation_select_nanodegree));
    } else {
      return new Pair<>(true, "");
    }
  }

  private void setupGrid() {
    mRecyclerView = (RecyclerView)imageSelectDialog.getCustomView();
    mRecyclerView.setHasFixedSize(false);
    mLayoutManager = new GridLayoutManager(this, 2);
    mRecyclerView.setLayoutManager(mLayoutManager);

    mAdapter = new CrawledGridAdapter(getApplicationContext());
    mRecyclerView.setAdapter(mAdapter);
  }

  private MaterialDialog getImageDialog() {
    return new MaterialDialog.Builder(this)
            .title(R.string.select_image_title)
            .customView(R.layout.grid_crawled_images, false)
            .buttonRippleColorRes(R.color.primary)
            .negativeColorRes(R.color.primary)
            .negativeText(R.string.select_image_decline)
            .build();
  }

  @Override
  public void finishImageResponse(Document out) {

  }
}