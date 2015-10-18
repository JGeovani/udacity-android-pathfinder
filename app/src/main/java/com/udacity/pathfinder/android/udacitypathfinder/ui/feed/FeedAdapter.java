package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final int VIEW_TYPE_GRID = 0;
  public static final int VIEW_TYPE_LIST = 1;

  private List<Article> articles = Collections.emptyList();
  private Context context;
  private int viewType;

  public FeedAdapter(Context context, int viewType) {
    this.context = context;
    this.viewType = viewType;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    switch (type) {
      case VIEW_TYPE_GRID:
        View gridView = inflater.inflate(R.layout.grid_item_article, parent, false);
        return new GridViewHolder(gridView);
      case VIEW_TYPE_LIST:
        View listView = inflater.inflate(R.layout.list_item_article, parent, false);
        return new ListViewHolder(listView);
      default:
        throw new IllegalArgumentException("View type does not exist");
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (getItemViewType(position)) {
      case VIEW_TYPE_GRID:
        bindGridViewHolder(holder, position);
        break;
      case VIEW_TYPE_LIST:
        bindListViewHolder(holder, position);
        break;
    }
  }

  private void bindGridViewHolder(RecyclerView.ViewHolder holder, int position) {
    GridViewHolder gridViewHolder = (GridViewHolder) holder;
    if (articles != null && !articles.isEmpty()) {
      Article article = articles.get(position);
      Glide.with(context).load(article.getImageUrl()).into(gridViewHolder.image);
      gridViewHolder.title.setText(article.getTitle());
    }
  }

  private void bindListViewHolder(RecyclerView.ViewHolder holder, int position) {
    ListViewHolder listViewHolder = (ListViewHolder) holder;
    if (articles != null && !articles.isEmpty()) {
      Article article = articles.get(position);
      Glide.with(context).load(article.getImageUrl()).into(listViewHolder.image);
      listViewHolder.title.setText(article.getTitle());
      listViewHolder.description.setText(article.getDescription());
    }
  }

  @Override public int getItemViewType(int position) {
    return viewType;
  }

  @Override public int getItemCount() {
    return articles.size();
  }

  public void updateArticles(List<Article> articles) {
    this.articles = articles;
    notifyDataSetChanged();
  }

  public static class ListViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.article_image) ImageView image;
    @Bind(R.id.article_title) TextView title;
    @Bind(R.id.article_description) TextView description;

    public ListViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  public static class GridViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.article_image) ImageView image;
    @Bind(R.id.article_title) TextView title;

    public GridViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}