package com.logicline.tech.stube.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.databinding.ItemPlaylistVideoBinding;
import com.logicline.tech.stube.models.PlayListVideo;
import com.logicline.tech.stube.models.PlayerPlayListItem;

import java.util.List;

public class PlaylistVideoAdapter extends RecyclerView.Adapter<PlaylistVideoAdapter.ItemViewHolder> {
    private static final String TAG = "PlaylistVideoAdapter";
    private final Context context;
    private List<PlayListVideo.Item> items;
    private ItemClickListener mItemClickListener;

    public PlaylistVideoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist_video, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PlayListVideo.Item item = items.get(position);

        String playlistTitle = item.snippet.title;
        String channelTitle = item.snippet.channelTitle;
        String thumbNailUrl = null;

        Log.d(TAG, "onBindViewHolder: " + new Gson().toJson(item));


        if (item.snippet.thumbnails != null)

            if (item.snippet.thumbnails.high != null)
                thumbNailUrl = item.snippet.thumbnails.high.url;
            else if (item.snippet.thumbnails.medium != null) {
                thumbNailUrl = item.snippet.thumbnails.medium.url;
            } else if (item.snippet.thumbnails.mydefault != null)
                thumbNailUrl = item.snippet.thumbnails.mydefault.url;

        holder.binding.tvPlaylistItemTitle.setText(playlistTitle);
        holder.binding.tvPlaylistItemChannelTitle.setText(channelTitle);

        if (thumbNailUrl != null)
            Glide.with(context)
                    .load(thumbNailUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.binding.ivPlayListItemThumbnail);
        else {
            Glide.with(context)
                    .load(R.drawable.image_placeholder)
                    .into(holder.binding.ivPlayListItemThumbnail);
        }

    }

    @Override
    public int getItemCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    public void setData(List<PlayListVideo.Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addData(List<PlayListVideo.Item> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface ItemClickListener {
        void onClick(PlayListVideo.Item item);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemPlaylistVideoBinding binding;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemPlaylistVideoBinding.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if (mItemClickListener != null) {
                mItemClickListener.onClick(items.get(position));
            }
        }
    }
}
