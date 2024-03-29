package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.data.models.Article;
import com.udacity.pathfinder.android.udacitypathfinder.ui.article.ArticleActivity;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

  public static final int VIEW_TYPE_GRID = 0;
  public static final int VIEW_TYPE_LIST = 1;
  public static final int VIEW_TYPE_LIKED = 2;

  private List<Article> articles = Collections.emptyList();
  private Context context;
  private int viewType;

  public FeedAdapter(Context context, int viewType) {
    this.context = context;
    this.viewType = viewType;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    switch (type) {
      case VIEW_TYPE_GRID:
        View gridView = inflater.inflate(R.layout.grid_item_article, parent, false);
        return new GridViewHolder(gridView);
      case VIEW_TYPE_LIST:
        View listView = inflater.inflate(R.layout.list_item_article, parent, false);
        return new ListViewHolder(listView);
      case VIEW_TYPE_LIKED:
        View likedView = inflater.inflate(R.layout.list_item_article, parent, false);
        return new LikedViewHolder(likedView);
      default:
        throw new IllegalArgumentException("View type does not exist");
    }
  }

  @Override public void onBindViewHolder(ViewHolder holder, final int position) {
    holder.view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (articles != null && !articles.isEmpty()) {
          Article article = articles.get(position);
          Intent intent = new Intent(context, ArticleActivity.class);
          intent.putExtra(ArticleActivity.KEY_ARTICLE_OBJECT_ID, article.getObjectId());
          context.startActivity(intent);
        }
      }
    });
    switch (getItemViewType(position)) {
      case VIEW_TYPE_GRID:
        bindGridViewHolder(holder, position);
        break;
      case VIEW_TYPE_LIST:
        bindListViewHolder(holder, position);
        break;
      case VIEW_TYPE_LIKED:
        bindLikedViewHolder(holder, position);
        break;
    }
  }

  private void bindGridViewHolder(ViewHolder holder, int position) {
    GridViewHolder gridViewHolder = (GridViewHolder) holder;
    if (articles != null && !articles.isEmpty()) {
      Article article = articles.get(position);
      Glide.with(context)
          .load(article.getImageUrl())
          .error(R.drawable.default_article_image)
          .into(gridViewHolder.image);
      gridViewHolder.title.setText(article.getTitle().trim());
      gridViewHolder.source.setText(article.getDomain());
    }
  }

  private void bindListViewHolder(ViewHolder holder, int position) {
    ListViewHolder listViewHolder = (ListViewHolder) holder;
    if (articles != null && !articles.isEmpty()) {
      Article article = articles.get(position);
      Glide.with(context)
          .load(article.getImageUrl())
          .error(R.drawable.default_article_image)
          .into(listViewHolder.image);
      listViewHolder.title.setText(article.getTitle().trim());
      listViewHolder.source.setText(article.getDomain());
      listViewHolder.likesCount.setText(Integer.toString(article.getLikes()));
    }
  }

  /**
   * Need to add code that determines if article has been liked by user.
   * @param holder
   * @param position
   */
  private void bindLikedViewHolder(ViewHolder holder, int position) {
    LikedViewHolder likedViewHolder = (LikedViewHolder) holder;
    if (articles != null && !articles.isEmpty()) {
      Article article = articles.get(position);
      Glide.with(context)
              .load(article.getImageUrl())
              .error(R.drawable.default_article_image)
              .into(likedViewHolder.image);
      likedViewHolder.title.setText(article.getTitle().trim());
      likedViewHolder.source.setText(article.getDomain());
      likedViewHolder.likesCount.setText(Integer.toString(article.getLikes()));
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

  public abstract static class ViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.article_image) ImageView image;
    @Bind(R.id.article_title) TextView title;
    @Bind(R.id.article_source) TextView source;
    @Bind(R.id.article_likes_count) TextView likesCount;
    public final View view;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
    }
  }

  public static class ListViewHolder extends ViewHolder {

    public ListViewHolder(View view) {
      super(view);
    }
  }

  public static class GridViewHolder extends ViewHolder {

    public GridViewHolder(View view) {
      super(view);
    }
  }

  public static class LikedViewHolder extends ViewHolder {
    public LikedViewHolder(View view) { super(view); }
  }
}