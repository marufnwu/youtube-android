package com.logicline.tech.stube.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.models.CommentThread;
import com.logicline.tech.stube.ui.activities.playerActivity.PlayerViewModel;
import com.logicline.tech.stube.ui.adapters.CommentsAdapter;

import java.util.List;

public class CommentBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "CommentBottomSheet";
    private List<CommentThread.Item> comments;
    private CommentsAdapter commentsAdapter;

    public void setData(List<CommentThread.Item> comments){
        this.comments = comments;
        if (commentsAdapter!= null){
            Log.d(TAG, "setData: is called");
            commentsAdapter.setData(comments);
            Log.d(TAG, "setData: " + comments.size());
        }else {
            Log.d(TAG, "setData: comment adapter is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_comment, container, false);

        RecyclerView commentRv = view.findViewById(R.id.rv_comments);
        commentRv.setLayoutManager(new LinearLayoutManager(getContext()));

        commentsAdapter = new CommentsAdapter(getContext());
        commentRv.setAdapter(commentsAdapter);

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
}
