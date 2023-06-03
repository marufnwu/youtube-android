package com.logicline.tech.stube.ui.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.models.CommentThread;
import com.logicline.tech.stube.ui.activities.channelActivity.ChannelActivity;
import com.logicline.tech.stube.ui.activities.playerActivity.PlayerViewModel;
import com.logicline.tech.stube.ui.adapters.CommentsAdapter;

import java.util.List;

public class CommentBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "CommentBottomSheet";
    private List<CommentThread.Item> comments;
    private CommentsAdapter commentsAdapter;
    private String videoId;
    private PlayerViewModel viewModel;
    private RecyclerView commentRv;
    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_comment, container, false);

        if (this.getArguments() != null)
            videoId = this.getArguments().getString(Constants.BOTTOM_SHEET_DATA_KEY);

        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        commentRv = view.findViewById(R.id.rv_comments);
        commentRv.setLayoutManager(new LinearLayoutManager(getContext()));

        commentsAdapter = new CommentsAdapter(getContext());
        commentsAdapter.setClickListener(new CommentsAdapter.ItemClickListener() {
            @Override
            public void onChannelClicked(CommentThread.Item item) {
                Intent intent = ChannelActivity.getChannelActivityIntent(
                        getActivity(),
                        item.snippet.topLevelComment.snippet.authorChannelId.value);
                startActivity(intent);
            }
        });


        commentRv.setAdapter(commentsAdapter);

        if (videoId != null) {
            viewModel.loadCommentThread(videoId);
        }

        initViewModelObserver();
        findEndOfRecyclerView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    private void initViewModelObserver() {
        viewModel.getCommentThread().observe(this, new Observer<CommentThread>() {
            @Override
            public void onChanged(CommentThread commentThread) {
                if (commentThread != null && commentsAdapter != null) {
                    commentsAdapter.setData(commentThread.items);
                }
            }
        });

        viewModel.getCommentThreadNextPage().observe(this, new Observer<CommentThread>() {
            @Override
            public void onChanged(CommentThread commentThread) {
                if (commentThread != null && commentsAdapter != null) {
                    commentsAdapter.addData(commentThread.items);
                    isLoading = false;
                }
            }
        });
    }

    private void findEndOfRecyclerView() {
        commentRv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.d(TAG, "onScrolled: dy " + dy);
                if (dy > 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager == null) {
                        Log.d(TAG, "onScrolled: layout manager is null");
                        return;
                    }

                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    Log.d(TAG, "onScrolled: total item count " + totalItemCount);
                    Log.d(TAG, "onScrolled: visible item count " + visibleItemCount);
                    Log.d(TAG, "onScrolled: first visible item position " + firstVisibleItemPosition);

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        // End of RecyclerView reached
                        Log.d(TAG, "onScrolled: last");

                        if (!isLoading) {
                            viewModel.loadCommentThreadNextPage(videoId);
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }
}
