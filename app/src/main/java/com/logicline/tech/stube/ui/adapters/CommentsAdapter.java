package com.logicline.tech.stube.ui.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.databinding.ItemCommentBinding;
import com.logicline.tech.stube.models.CommentThread;

import java.util.Date;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.itemViewHolder>{
    private static final String TAG = "CommentsAdapter";
    private List<CommentThread.Item> comments;
    private ItemClickListener clickListener;
    private Context context;

    public CommentsAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        if (comments != null){
            CommentThread.Item item = comments.get(position);
            CommentThread.TopLevelComment topLevelComment = item.snippet.topLevelComment;

            String commentChannelName = topLevelComment.snippet.authorDisplayName;
            String commentChannelImage = topLevelComment.snippet.authorProfileImageUrl;
            String comment = topLevelComment.snippet.textDisplay;
            /*String lineSep = System.getProperty("line.separator");
            comment = comment.replaceAll("<br>", lineSep);*/

            Spanned commentSpanned = HtmlCompat.fromHtml(comment, HtmlCompat.FROM_HTML_MODE_LEGACY);

            int likeCount = topLevelComment.snippet.likeCount;

            Log.d(TAG, "onBindViewHolder: channel name " + commentChannelName);
            Log.d(TAG, "onBindViewHolder: channel Image " + commentChannelImage);
            Log.d(TAG, "onBindViewHolder: comment " + comment);
            Log.d(TAG, "onBindViewHolder: likeCount " + likeCount);
            Log.d(TAG, "onBindViewHolder: size " + comments.size());

            holder.commentBinding.tvCommentChannelName.setText(commentChannelName);
            holder.commentBinding.tvComment.setText(commentSpanned);
            holder.commentBinding.tvComment.setMovementMethod(LinkMovementMethod.getInstance());
            holder.commentBinding.tvCommentLikes.setText(likeCount + "");

            Glide.with(context)
                    .load(commentChannelImage)
                    .placeholder(R.drawable.ic_profile)
                    .into(holder.commentBinding.ivCommentChannelAvatar);
        }

    }

    @Override
    public int getItemCount() {
        if (comments == null)
            return 0;
        return comments.size();
    }

    public void setData(List<CommentThread.Item> data){
        this.comments = data;
        notifyDataSetChanged();
    }
    public void addData(List<CommentThread.Item> data){
        this.comments.addAll(data);
        notifyDataSetChanged();
    }
    public void setClickListener(ItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public interface ItemClickListener{
        void onChannelClicked(CommentThread.Item item);
    }

    class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemCommentBinding commentBinding;

        public itemViewHolder(@NonNull View itemView) {
            super(itemView);

            commentBinding = ItemCommentBinding.bind(itemView);

            commentBinding.ivCommentChannelAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if (comments != null){
                CommentThread.Item item = comments.get(position);
                if (clickListener != null){
                    clickListener.onChannelClicked(item);
                }
            }
        }
    }
}
