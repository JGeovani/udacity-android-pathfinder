package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.udacity.pathfinder.android.udacitypathfinder.R;

public class FeedActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
    Resources res = getResources();

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(res.getString(R.string.title_activity_feed));
    setSupportActionBar(toolbar);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.addTab(tabLayout.newTab().setText(res.getString(R.string.title_tab_grid)));
    tabLayout.addTab(tabLayout.newTab().setText(res.getString(R.string.title_tab_list)));

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new FeedAdapter(this, recyclerView));

  }
}
