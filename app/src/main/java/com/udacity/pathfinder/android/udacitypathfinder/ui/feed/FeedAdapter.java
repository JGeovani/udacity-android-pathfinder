package com.udacity.pathfinder.android.udacitypathfinder.ui.feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;
import com.udacity.pathfinder.android.udacitypathfinder.R;
import com.udacity.pathfinder.android.udacitypathfinder.parse.Article;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
  private ParseQueryAdapter<Article> parseAdapter;
  private FeedAdapter thingsAdapter = this;
  private ViewGroup parseParent;

  public FeedAdapter(Context context, ViewGroup parentIn) {
    parseParent = parentIn;
    parseAdapter = new ParseQueryAdapter<Article>(context, "Articles") {

      @Override
      public View getItemView(Article article, View view, ViewGroup parent) {
        if (view == null) {
          view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        }
        super.getItemView(article, view, parent);

        ImageView image = (ImageView) view.findViewById(R.id.article_image);
        TextView title = (TextView) view.findViewById(R.id.article_title);
        TextView description = (TextView) view.findViewById(R.id.article_beginning);

        Picasso.with(getContext()).load(article.getImageUrl()).into(image);
        title.setText(article.getTitle());
        description.setText(article.getDescription());

        return view;
      }
    };
    parseAdapter.addOnQueryLoadListener(new OnQueryLoadListener());
    parseAdapter.loadObjects();
  }

  @Override
  public FeedViewHolder onCreateViewHolder(ViewGroup parent, int i) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
    return new FeedViewHolder(v);
  }


  @Override
  public void onBindViewHolder(FeedViewHolder holder, int i) {
    parseAdapter.getView(i, holder.view, parseParent);
  }

  @Override
  public int getItemCount() {
    return parseAdapter.getCount();
  }

  public static class FeedViewHolder extends RecyclerView.ViewHolder {
    public View view;

    public FeedViewHolder(View v) {
      super(v);
      view = v;
    }
  }

  public class OnQueryLoadListener implements ParseQueryAdapter.OnQueryLoadListener<Article> {

    public void onLoading() {
    }

    public void onLoaded(List<Article> objects, Exception e) {
      thingsAdapter.notifyDataSetChanged();
    }
  }
}