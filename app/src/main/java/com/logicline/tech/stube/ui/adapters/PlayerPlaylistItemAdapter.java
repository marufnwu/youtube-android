package com.logicline.tech.stube.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.databinding.ItemPlaylistVideoBinding;
import com.logicline.tech.stube.models.PlayerPlayListItem;

import java.util.List;

public class PlayerPlaylistItemAdapter extends RecyclerView.Adapter<PlayerPlaylistItemAdapter.ItemViewHolder>{
    private List<PlayerPlayListItem> data;
    private Context context;
    private ItemClickListener itemClickListener;

    public PlayerPlaylistItemAdapter(Context context, List<PlayerPlayListItem> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist_video, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PlayerPlayListItem item = data.get(position);

        holder.binding.tvPlaylistItemTitle.setText(item.getVideoTitle());
        holder.binding.tvPlaylistItemChannelTitle.setText(item.getChannelName());

        Glide.with(context)
                .load(item.getVideoImageUrl())
                .placeholder(R.drawable.image_placeholder)
                .into(holder.binding.ivPlayListItemThumbnail);
        /*if (item.isPlaying()){
            holder.binding.cvRoot.setCardBackgroundColor(context.getResources().getColor(R.color.card_background));
        }*/
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        return data.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(PlayerPlayListItem item);
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
            if (itemClickListener != null) {
                PlayerPlayListItem item = data.get(position);
                item.setPosition(position);
                itemClickListener.onItemClick(item);
            }
        }
    }
}
