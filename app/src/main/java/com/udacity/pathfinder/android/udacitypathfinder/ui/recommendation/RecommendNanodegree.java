package com.udacity.pathfinder.android.udacitypathfinder.ui.recommendation;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Nanodegree;
import com.udacity.pathfinder.android.udacitypathfinder.ui.article.ArticleActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecommendNanodegree extends AppCompatActivity {
  @Bind(R.id.tv_nano_short_description)
  TextView tv_short_discription;
  @Bind(R.id.tv_toolbar)
  Toolbar tv_toolbar;
  @Bind(R.id.iv_nano_logo)
  ImageView iv_nano_logo;
  @Bind(R.id.tv_nano_title)
  TextView tv_nano_title;
  @Bind(R.id.btn_learn_more)
  Button btn_learn_more;
  @Bind(R.id.pb_like_1)
  ProgressBar pb_like_1;
  @Bind(R.id.pb_like_2)
  ProgressBar pb_like_2;
  @Bind(R.id.pb_like_3)
  ProgressBar pb_like_3;
  @Bind(R.id.tv_like_1)
  TextView tv_like_1;
  @Bind(R.id.tv_like_2)
  TextView tv_like_2;
  @Bind(R.id.tv_like_3)
  TextView tv_like_3;
  @Bind(R.id.tv_total_likes_1)
  TextView tv_total_likes_1;
  @Bind(R.id.tv_total_likes_2)
  TextView tv_total_likes_2;
  @Bind(R.id.tv_total_likes_3)
  TextView tv_total_likes_3;
  String[][] recommendedNanodegrees;
  String degreeTitle, degreeUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Recommend recommend = new Recommend(this);
    setContentView(R.layout.activity_recommendation);
    ButterKnife.bind(this);
    tv_toolbar.setTitle(R.string.rec_toolbar_title);
    // remove notification
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    notificationManager.cancel(101);
    // Obtain recommended nanodegrees
    recommendedNanodegrees = recommend.nanodegree();
    requestNanodegrees();
  }

  private void requestNanodegrees() {
    ParseClient.request(ParseConstants.NANODEGREE_CLASS_NAME, true, new RequestCallback<Nanodegree>() {
      @Override
      public void onResponse(List<Nanodegree> nanodegree, ParseException e) {
        if (e == null && nanodegree != null) {
          if (!nanodegree.isEmpty()) {
            for (int i = 0; i < nanodegree.size(); i++) {
              final Nanodegree degreeObject = nanodegree.get(i);
              String degreeId = degreeObject.getDegreeId();
              List<String> tracks = degreeObject.getTracks();

              //top three recommendations
              for (int j = 0; j < 3; j++) {

                if (degreeId.equals(recommendedNanodegrees[j][0])) {
                  int maxProgress = Integer.parseInt(recommendedNanodegrees[0][1]);
                  int progress;
                  Drawable drawable;
                  // getResources() to support >21 API
                  Resources resource = getResources();
                  progress = Integer.parseInt(recommendedNanodegrees[j][1]);
                  // Some data is missing from nanodegree api regarding tracks, we will hardcode
                  String degreeTrack;
                  switch (degreeId) {
                    case "nd000":
                      degreeTrack = "Intro to Programming";
                      break;
                    case "nd006":
                      degreeTrack = "Beginning iOS";
                      break;
                    case "nd007":
                      degreeTrack = "Tech Entrepreneur";
                      break;
                    default:
                      degreeTrack = tracks.get(1);
                  }

                  // configure nanodegree recommendation UI
                  switch (j) {
                    case 0:
                      //configure #1 nanodegree recommendation
                      Glide.with(getBaseContext())
                        .load(degreeObject.getImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_nano_logo);
                      tv_nano_title.setText(ArticleActivity.capitalizeString(degreeObject.getDegreeTitle()));
                      tv_short_discription.setText(degreeObject.getShortSummary());
                      degreeTitle = degreeObject.getDegreeTitle();
                      degreeUrl = degreeObject.getDegreeUrl();
                      btn_learn_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          Intent i = new Intent(getBaseContext(), LearnMoreWebView.class);
                          i.putExtra("url", degreeUrl);
                          i.putExtra("title", ArticleActivity.capitalizeString(degreeTitle));
                          startActivity(i);
                        }
                      });

                      tv_like_1.setText(degreeTrack);
                      tv_total_likes_1.setText(String.valueOf(maxProgress));
                      drawable = resource.getDrawable(R.drawable.progress_bar_1_background);
                      pb_like_1.setProgress(progress);
                      pb_like_1.setMax(maxProgress);
                      pb_like_1.setProgressDrawable(drawable);
                      break;
                    case 1:
                      drawable = resource.getDrawable(R.drawable.progress_bar_2_background);
                      pb_like_2.setProgress(progress);
                      pb_like_2.setMax(maxProgress);
                      pb_like_2.setProgressDrawable(drawable);
                      tv_total_likes_2.setText(String.valueOf(progress));
                      tv_like_2.setText(degreeTrack);
                      break;
                    case 2:
                      drawable = resource.getDrawable(R.drawable.progress_bar_3_background);
                      pb_like_3.setProgress(progress);
                      pb_like_3.setMax(maxProgress);
                      pb_like_3.setProgressDrawable(drawable);
                      tv_total_likes_3.setText(String.valueOf(progress));
                      tv_like_3.setText(degreeTrack);
                      break;
                  }
                }
              }
            }
          }
        } else {
          Timber.e(e, "Error occurred while retrieving recommendation");
        }
      }
    });
  }
}
