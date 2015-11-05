package com.udacity.pathfinder.android.udacitypathfinder.ui.recommendation;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseClient;
import com.udacity.pathfinder.android.udacitypathfinder.data.ParseConstants;
import com.udacity.pathfinder.android.udacitypathfinder.data.Recommend;
import com.udacity.pathfinder.android.udacitypathfinder.data.RequestCallback;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Nanodegree;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecommendNanodegree extends AppCompatActivity {
  @Bind(R.id.tv_nano_short_description)
  TextView tv_short_discription;
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
  String[][] recommendedNanodegrees;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Recommend recommend = new Recommend(this);
    setContentView(R.layout.activity_recommendation);
    ButterKnife.bind(this);

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
                        .into(iv_nano_logo);
                      tv_nano_title.setText(degreeObject.getDegreeTitle());
                      tv_short_discription.setText(degreeObject.getShortSummary());
                      btn_learn_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          Uri uri = Uri.parse(degreeObject.getDegreeUrl());
                          Intent startBrowser = new Intent(Intent.ACTION_VIEW, uri);
                          startActivity(startBrowser);
                        }
                      });

                      tv_like_1.setText(degreeTrack);
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
                      tv_like_2.setText(degreeTrack);
                      break;
                    case 2:
                      drawable = resource.getDrawable(R.drawable.progress_bar_3_background);
                      pb_like_3.setProgress(progress);
                      pb_like_3.setMax(maxProgress);
                      pb_like_3.setProgressDrawable(drawable);
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
