package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.pathfinder.android.udacitypathfinder.R;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

  @Override public FeedViewHolder onCreateViewHolder(ViewGroup parent, int i) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    return new FeedViewHolder(inflater.inflate(R.layout.list_item_article, parent, false));
  }

  @Override public void onBindViewHolder(FeedViewHolder holder, int i) {
    // placeholder values
    holder.image.setImageResource(R.mipmap.ic_launcher);
    holder.title.setText("Article Title Here");
    holder.beginning.setText("Beginning of article maximum 30 characters");
  }

  @Override public int getItemCount() {
    // placeholder count
    return 20;
  }

  public static class FeedViewHolder extends RecyclerView.ViewHolder {

    private ImageView image;
    private TextView title;
    private TextView beginning;

    public FeedViewHolder(View view) {
      super(view);
      image = (ImageView) view.findViewById(R.id.article_image);
      title = (TextView) view.findViewById(R.id.article_title);
      beginning = (TextView) view.findViewById(R.id.article_beginning);
    }
  }
}
