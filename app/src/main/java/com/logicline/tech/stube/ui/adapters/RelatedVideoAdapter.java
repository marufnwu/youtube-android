package com.logicline.tech.stube.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.databinding.ItemHomeVideoBinding;
import com.logicline.tech.stube.models.RelatedVideo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RelatedVideoAdapter extends RecyclerView.Adapter<RelatedVideoAdapter.videoViewHolder> {
    private final Context context;
    private ItemClickListener mItemClickListener;
    private List<RelatedVideo.Item> items;

    public RelatedVideoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public videoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_home_video, parent, false);

        return new videoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull videoViewHolder holder, int position) {
        //get data
        RelatedVideo.Item item = items.get(position);
        String videoTitle = item.snippet.title;
        String channelTitle = item.snippet.channelTitle;
        String thumbnailUrl = item.snippet.thumbnails.high.url;
        Date publishDate = item.snippet.publishedAt;

        holder.itemBinding.tvVideoTitle.setText(videoTitle);
        holder.itemBinding.tvChannelName.setText(channelTitle);

        Glide.with(context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.itemBinding.ivVideoItemThumbnail);
        //holder.itemBinding.ivChannelAvatar.setImageDrawable(context.getDrawable(R.drawable.image_placeholder));
        holder.itemBinding.ivChannelAvatar.setImageResource(R.mipmap.ic_launcher_round);

    }

    @Override
    public int getItemCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    public void setData(List<RelatedVideo.Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addData(List<RelatedVideo.Item> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    public interface ItemClickListener {
        void onVideoClick(RelatedVideo.Item item);

        void onChannelClick(RelatedVideo.Item item);
    }

    class videoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemHomeVideoBinding itemBinding;

        public videoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemHomeVideoBinding.bind(itemView);

            itemBinding.tvVideoTitle.setOnClickListener(this);
            itemBinding.ivVideoItemThumbnail.setOnClickListener(this);
            itemBinding.tvChannelName.setOnClickListener(this);
            itemBinding.ivChannelAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            RelatedVideo.Item item = items.get(position);
            if (mItemClickListener != null) {
                if (v == itemBinding.ivVideoItemThumbnail || v == itemBinding.tvVideoTitle)
                    mItemClickListener.onVideoClick(item);
                else if (v == itemBinding.ivChannelAvatar || v == itemBinding.tvChannelName) {
                    mItemClickListener.onChannelClick(item);
                }
            }
        }
    }

}

