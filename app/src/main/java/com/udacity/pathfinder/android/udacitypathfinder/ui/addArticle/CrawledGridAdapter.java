package com.udacity.pathfinder.android.udacitypathfinder.ui.addArticle;

/**
 * Created by curt on 11/10/15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.udacity.pathfinder.android.udacitypathfinder.R;

import java.util.ArrayList;
import java.util.List;

public class CrawledGridAdapter extends RecyclerView.Adapter<CrawledGridAdapter.ViewHolder>
        implements View.OnClickListener {

    List<CrawledImageItem> mItems;
    Context context;

    public interface mClickListener {
        public void mClick(View v, int position);
    }

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
        CrawledImageItem item = mItems.get(position);
        viewHolder.tvName.setText(item.getName());

        Glide.with(context)
                .load(item.getThumbnail())
                .into(viewHolder.imgThumbnail);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(CrawledImageItem item) {
        mItems.add(item);
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgThumbnail;
        public TextView tvName;
        public RelativeLayout ciWrapper;

        public ViewHolder(View itemView) {
            super(itemView);
            ciWrapper = (RelativeLayout)itemView.findViewById(R.id.crawled_image_wrapper);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.crawled_image_thumbnail);
            tvName = (TextView)itemView.findViewById(R.id.crawled_image_name);
        }
    }
}

