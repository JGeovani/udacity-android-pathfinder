package com.udacity.pathfinder.android.udacitypathfinder.ui.recommendation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.Recommend;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecommendNanodegree extends AppCompatActivity {
@Bind(R.id.tv_nano_short_description)
TextView tv_short_discription;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Recommend recommend = new Recommend(this);
    setContentView(R.layout.activity_recommendation);
    ButterKnife.bind(this);

    /**
     * Used for testing
     */
    String description = "Our Recommendation:\n";
    String[][] recommendedNanodegrees = recommend.nanodegree();
    for (int i=0;i<recommendedNanodegrees.length;i++){
      description+="#"+(i+1)+": Nanodegree "+recommendedNanodegrees[i][0]+" ("+recommendedNanodegrees[i][1]+" likes)\n";
    }
    tv_short_discription.setText(description);
  }

}
