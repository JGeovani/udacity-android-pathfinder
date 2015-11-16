package com.udacity.pathfinder.android.udacitypathfinder.ui.addArticle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.pathfinder.android.udacitypathfinder.R;

import java.util.ArrayList;
import java.util.List;

public class CrawledGridAdapter extends RecyclerView.Adapter<CrawledGridAdapter.ViewHolder> {

  List<CrawledImageItem> mItems;
  Context context;

  public CrawledGridAdapter(Context context) {
    super();
    this.context = context;
    mItems = new ArrayList<CrawledImageItem>();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
    View v = LayoutInflater.from(viewGroup.getContext())
      .inflate(R.layout.grid_crawled_image, viewGroup, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int position) {
    final CrawledImageItem item = mItems.get(position);
    viewHolder.tvName.setText(item.getName());
    viewHolder.view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d("TEST", "Position = " + position);
        AddArticleActivity.setImageUrl(mItems.get(position).getThumbnail());
      }
    });
    Glide.with(context)
      .load(item.getThumbnail())
      .into(viewHolder.imgThumbnail);

  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }

  public void add(CrawledImageItem item) {

  }

  public void add(int position, CrawledImageItem item) {
    mItems.add(position, item);
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgThumbnail;
    public TextView tvName;
    public LinearLayout ciWrapper;
    public final View view;

    public ViewHolder(View itemView) {
      super(itemView);
      ciWrapper = (LinearLayout) itemView.findViewById(R.id.crawled_image_wrapper);
      imgThumbnail = (ImageView) itemView.findViewById(R.id.crawled_image_thumbnail);
      tvName = (TextView) itemView.findViewById(R.id.crawled_image_name);
      this.view = itemView;
    }
  }
}

