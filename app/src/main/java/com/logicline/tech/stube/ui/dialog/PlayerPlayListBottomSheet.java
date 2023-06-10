package com.logicline.tech.stube.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.models.PlayListVideo;
import com.logicline.tech.stube.models.PlayerPlayListItem;
import com.logicline.tech.stube.ui.adapters.PlayerPlaylistItemAdapter;
import com.logicline.tech.stube.ui.adapters.PlaylistVideoAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlayerPlayListBottomSheet extends BottomSheetDialogFragment {
    private List<PlayerPlayListItem> data = new ArrayList<>();
    private RecyclerView playlistRv;
    private PlaylistVideoClickListener clickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_playlist, container, false);

        if (this.getArguments() != null){
            List<String> dataString = this.getArguments().getStringArrayList(Constants.PLAYLIST_BOTTOM_SHEET_DATA_KEY);
            for (String st : dataString){
                data.add(new Gson().fromJson(st, PlayerPlayListItem.class));
            }
        }

        playlistRv = view.findViewById(R.id.rv_player_playlist);
        playlistRv.setLayoutManager(new LinearLayoutManager(getContext()));

        PlayerPlaylistItemAdapter adapter = new PlayerPlaylistItemAdapter(getContext(), data);

        adapter.setItemClickListener(new PlayerPlaylistItemAdapter.ItemClickListener() {
            @Override
            public void onItemClick(PlayerPlayListItem item) {
                //Toast.makeText(getContext(), "item Clicked", Toast.LENGTH_SHORT).show();
                if (clickListener != null){
                    clickListener.onClick(item);
                }
            }
        });
        playlistRv.setAdapter(adapter);

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

    public interface PlaylistVideoClickListener{
        void onClick(PlayerPlayListItem item);
    }

    public void setPlayListVideoClickListener(PlaylistVideoClickListener videoClickListener){
        this.clickListener = videoClickListener;
    }

}
