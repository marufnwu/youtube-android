package com.logicline.tech.stube.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.databinding.ItemChannelVideoBinding;
import com.logicline.tech.stube.models.ChannelVideo;
import com.logicline.tech.stube.utils.Utils;

import java.util.List;

public class ChannelVideoAdapter extends RecyclerView.Adapter<ChannelVideoAdapter.itemViewHolder>{
    private static final String TAG = "ChannelVideoAdapter";
    ItemClickListener itemClickListener;
    List<ChannelVideo.Item> items;
    Context context;

    public ChannelVideoAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_channel_video, parent, false);
        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        ChannelVideo.Item item = items.get(position);
        String thumbNail = item.snippet.thumbnails.high.url;
        String title = item.snippet.title;
        String publishDate = Utils.getDateString(item.snippet.publishedAt);

        holder.binding.tvChannelVideoTitle.setText(title);
        holder.binding.tvChannelVideoPublishDate.setText(publishDate);
        Glide.with(context)
                .load(thumbNail)
                .into(holder.binding.ivChannelVideoThumbnail);
    }

    @Override
    public int getItemCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    public void setData(List<ChannelVideo.Item> items){
        this.items = items;
        notifyDataSetChanged();
    }
    public void addData(List<ChannelVideo.Item> items){
        Log.d(TAG, "getNextPage addData: is called");
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    public void addItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onclick(ChannelVideo.Item item);
    }
    class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemChannelVideoBinding binding;
        public itemViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemChannelVideoBinding.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null){
                int position = getAbsoluteAdapterPosition();
                itemClickListener.onclick(items.get(position));
            }
        }
    }
}
