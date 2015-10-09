package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.parse.Article;

import java.util.Collections;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

  private List<Article> articles = Collections.emptyList();

  @Override public FeedViewHolder onCreateViewHolder(ViewGroup parent, int i) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    return new FeedViewHolder(inflater.inflate(R.layout.list_item_article, parent, false));
  }

  @Override public void onBindViewHolder(FeedViewHolder holder, int pos) {
    if (articles != null && !articles.isEmpty()) {
      Article article = articles.get(pos);
      holder.image.setImageResource(R.mipmap.ic_launcher);
      holder.title.setText(article.getTitle());
      holder.description.setText(article.getDescription());
    }
  }

  @Override public int getItemCount() {
    return articles.size();
  }

  public void updateArticles(List<Article> articles) {
    this.articles = articles;
    notifyDataSetChanged();
  }

  public static class FeedViewHolder extends RecyclerView.ViewHolder {

    private ImageView image;
    private TextView title;
    private TextView description;

    public FeedViewHolder(View view) {
      super(view);
      image = (ImageView) view.findViewById(R.id.article_image);
      title = (TextView) view.findViewById(R.id.article_title);
      description = (TextView) view.findViewById(R.id.article_description);
    }
  }
}