package com.udacity.pathfinder.android.udacitypathfinder.ui.addArticle;

/**
 * Created by curt on 11/10/15.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.pathfinder.android.udacitypathfinder.R;

import java.util.ArrayList;
import java.util.List;

public class CrawledGridAdapter extends RecyclerView.Adapter<CrawledGridAdapter.ViewHolder> {

    List<CrawledImageItem> mItems;

    public CrawledGridAdapter() {
        super();
        mItems = new ArrayList<CrawledImageItem>();
        CrawledImageItem item = new CrawledImageItem();
        item.setName("This is a crawled image");
        item.setThumbnail(R.drawable.android_logo);
        mItems.add(item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_crawled_image, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        CrawledImageItem item = mItems.get(i);
        viewHolder.tvName.setText(item.getName());
        viewHolder.imgThumbnail.setImageResource(item.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgThumbnail;
        public TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.crawled_image_thumbnail);
            tvName = (TextView)itemView.findViewById(R.id.crawled_image_name);
        }
    }
}

