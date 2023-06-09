package com.logicline.tech.stube.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.databinding.ItemChannelBinding;
import com.logicline.tech.stube.databinding.ItemHomeVideoBinding;
import com.logicline.tech.stube.databinding.ItemPlaylistBinding;
import com.logicline.tech.stube.models.SearchItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private SearchItemAdapter.ItemClickListener mItemClickListener;
    private List<SearchItem.Item> items;

    public SearchItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

        SearchItem.Item item = items.get(position);
        if (Objects.equals(item.id.kind, "youtube#channel")) {
            return viewType.CHANNEL.ordinal();
        } else if (Objects.equals(item.id.kind, "youtube#playlist")) {
            return viewType.PLAYLIST.ordinal();
        } else
            return viewType.VIDEO.ordinal();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SearchItemAdapter.viewType.CHANNEL.ordinal()) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_channel, parent, false);
            return new ChannelViewHolder(view);
        } else if (viewType == SearchItemAdapter.viewType.PLAYLIST.ordinal()) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_playlist, parent, false);
            return new PlaylistViewHolder(view);
        } else {

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_home_video, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //get data
        SearchItem.Item item = items.get(position);
        if (getItemViewType(position) == viewType.PLAYLIST.ordinal()) {
            ((PlaylistViewHolder) holder).playlistBinding.tvPlaylistTitle.setText(item.snippet.title);
            ((PlaylistViewHolder) holder).playlistBinding.tvPlaylistChannelName.setText(item.snippet.channelTitle);
            Glide.with(context)
                    .load(item.snippet.thumbnails.high.url)
                    .placeholder(R.drawable.image_placeholder)
                    .into(((PlaylistViewHolder) holder).playlistBinding.ivPlaylistThumbnail);

            ((PlaylistViewHolder) holder).playlistBinding.ivPlaylistChannelAvatar
                    .setImageResource(R.mipmap.ic_launcher_round);

        } else if (getItemViewType(position) == viewType.CHANNEL.ordinal()) {
            ((ChannelViewHolder) holder).channelBinding.tvChannelTitle.setText(item.snippet.title);
            ((ChannelViewHolder) holder).channelBinding.tvChannelDescription.setText(item.snippet.description);
            Glide.with(context)
                    .load(item.snippet.thumbnails.high.url)
                    .placeholder(R.drawable.image_placeholder)
                    .into(((ChannelViewHolder) holder).channelBinding.ivChannelThumbnail);
        } else {
            ((VideoViewHolder) holder).videoBinding.tvVideoTitle.setText(item.snippet.title);
            ((VideoViewHolder) holder).videoBinding.tvChannelName.setText(item.snippet.channelTitle);

            Glide.with(context)
                    .load(item.snippet.thumbnails.high.url)
                    .placeholder(R.drawable.image_placeholder)
                    .into(((VideoViewHolder) holder).videoBinding.ivVideoItemThumbnail);
            //holder.itemBinding.ivChannelAvatar.setImageDrawable(context.getDrawable(R.drawable.image_placeholder));
            ((VideoViewHolder) holder).videoBinding.ivChannelAvatar
                    .setImageResource(R.mipmap.ic_launcher_round);
        }

        //get data
        /*SearchItem.Item item = items.get(position);
        String videoTitle = item.snippet.title;
        String channelTitle = item.snippet.channelTitle;
        String thumbnailUrl = item.snippet.thumbnails.high.url;
        Date publishDate = item.snippet.publishedAt;

        holder.itemVideoBinding.tvVideoTitle.setText(videoTitle);
        holder.itemVideoBinding.tvChannelName.setText(channelTitle + " . " + Utils.getDateString(publishDate));

        Glide.with(context)
                .load(thumbnailUrl)
                .into(holder.itemVideoBinding.ivVideoItemThumbnail);
        //holder.itemBinding.ivChannelAvatar.setImageDrawable(context.getDrawable(R.drawable.image_placeholder));
        holder.itemVideoBinding.ivChannelAvatar.setImageResource(R.mipmap.ic_launcher_round);*/

    }

    @Override
    public int getItemCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    public void setData(List<SearchItem.Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addData(List<SearchItem.Item> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setItemClickListener(SearchItemAdapter.ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    enum viewType {
        VIDEO,
        CHANNEL,
        PLAYLIST
    }


    public interface ItemClickListener {
        void onClickVideo(SearchItem.Item item);

        void onClickPlaylist(SearchItem.Item item);

        void onClickChannel(SearchItem.Item item);
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemHomeVideoBinding videoBinding;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoBinding = ItemHomeVideoBinding.bind(itemView);

            videoBinding.tvVideoTitle.setOnClickListener(this);
            videoBinding.tvChannelName.setOnClickListener(this);
            videoBinding.ivChannelAvatar.setOnClickListener(this);
            videoBinding.ivVideoItemThumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            SearchItem.Item item = items.get(position);
            int viewId = v.getId();
            if (mItemClickListener != null)
                if (viewId == R.id.iv_video_item_thumbnail || viewId == R.id.tv_video_title) {
                    mItemClickListener.onClickVideo(item);
                } else if (viewId == R.id.iv_channel_avatar || viewId == R.id.tv_channel_name) {
                    mItemClickListener.onClickChannel(item);
                }

        }
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemPlaylistBinding playlistBinding;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistBinding = ItemPlaylistBinding.bind(itemView);

            playlistBinding.ivPlaylistThumbnail.setOnClickListener(this);
            playlistBinding.ivPlaylistChannelAvatar.setOnClickListener(this);
            playlistBinding.tvPlaylistTitle.setOnClickListener(this);
            playlistBinding.tvPlaylistChannelName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            SearchItem.Item item = items.get(position);
            int viewId = v.getId();
            if (mItemClickListener != null) {
                if (viewId == R.id.iv_playlist_thumbnail || viewId == R.id.tv_playlist_title) {
                    mItemClickListener.onClickPlaylist(item);
                } else if (viewId == R.id.iv_playlist_channel_avatar || viewId == R.id.tv_playlist_channel_name) {
                    mItemClickListener.onClickChannel(item);
                }
            }
        }
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemChannelBinding channelBinding;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);

            channelBinding = ItemChannelBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if (mItemClickListener != null) {
                mItemClickListener.onClickChannel(items.get(position));
            }
        }
    }
}
