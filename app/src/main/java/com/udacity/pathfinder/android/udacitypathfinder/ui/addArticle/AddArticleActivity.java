package com.udacity.pathfinder.android.udacitypathfinder.ui.addArticle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.ui.feed.FeedActivity;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class AddArticleActivity extends AppCompatActivity {

  @BindString(R.string.title_activity_add_article) String ADD_ARTICLE_ACTIVITY_TITLE;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.nanodegrees) TextView nanodegrees;

  @State Integer[] selections = new Integer[]{};
  @State String selectionsText;

  private MaterialDialog nanodegreesDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_article);
    Icepick.restoreInstanceState(this, savedInstanceState);
    ButterKnife.bind(this);

    toolbar.setTitle(ADD_ARTICLE_ACTIVITY_TITLE);
    setSupportActionBar(toolbar);

    if (selectionsText != null) nanodegrees.setText(selectionsText);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_add_article, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_cancel) {
      Intent intent = new Intent(this, FeedActivity.class);
      startActivity(intent);
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
        .itemsCallbackMultiChoice(selections, new MaterialDialog.ListCallbackMultiChoice() {
          @Override public boolean onSelection(MaterialDialog materialDialog, Integer[] integers,
              CharSequence[] values) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0, n = values.length; i < n; i++) {
              builder.append(values[i]);
              if (!(i == n - 1)) builder.append(", ");
            }
            selectionsText = builder.toString();
            nanodegrees.setText(selectionsText);
            selections = integers;
            return true;
          }
        })
        .positiveText(R.string.dialog_nanodegrees_positive_text)
        .negativeText(R.string.dialog_nanodegrees_negative_text)
        .show();
  }
}
