package com.logicline.tech.stube.ui.activities.playlistActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.databinding.ActivityPlaylistBinding;
import com.logicline.tech.stube.models.PlayListVideo;
import com.logicline.tech.stube.models.PlaylistData;
import com.logicline.tech.stube.ui.activities.playerActivity.PlayerActivity;
import com.logicline.tech.stube.ui.adapters.PlaylistVideoAdapter;

import java.util.Objects;

public class PlaylistActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PlaylistActivity";
    private PlaylistData intentDAta;
    private ActivityPlaylistBinding binding;
    private PlaylistVideoAdapter adapter;
    private PlaylistViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);

        Intent intent = getIntent();
        if (intent != null){
            String dataString = intent.getStringExtra(Constants.PLAYLIST_ACTIVITY_DATA_KEY);
            intentDAta = new Gson().fromJson(dataString, PlaylistData.class);
        }

        Log.d(TAG, "onCreate: playlist id " + intentDAta.getPlaylistId());

        initViews();
    }
    private void initViews(){
        binding.tvPlaylistTitle.setText(intentDAta.getPlayListTitle());
        binding.tvChannelTitle.setText(intentDAta.getChannelTitle());
        Glide.with(this).load(intentDAta.getPlayListThumbnail()).into(binding.ivPlaylistThumbnail);

        binding.rvPlaylistVideo.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaylistVideoAdapter(this);
        binding.rvPlaylistVideo.setAdapter(adapter);

        adapter.setItemClickListener(new PlaylistVideoAdapter.ItemClickListener() {
            @Override
            public void onClick(PlayListVideo.Item item) {
                if (Objects.equals(item.snippet.title, "Private video")){
                    Toast.makeText(getApplicationContext(), "Private video", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                    intent.putExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY, item.snippet.resourceId.videoId);
                    startActivity(intent);
                }

            }
        });

        binding.btnPlayAll.setOnClickListener(this);

        initViewModelObserver();

        viewModel.loadPlayListVideo(intentDAta.getPlaylistId());
    }
    private void initViewModelObserver(){
        viewModel.getPlayListVideo().observe(this, new Observer<PlayListVideo>() {
            @Override
            public void onChanged(PlayListVideo playListVideo) {
                if (playListVideo != null){
                    adapter.setData(playListVideo.items);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnPlayAll){
            Toast.makeText(getApplicationContext(), "play All clicked", Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent getPlayListIntent(Context context, PlaylistData data){
        Intent intent = new Intent(context, PlaylistActivity.class);
        String dataString = new Gson().toJson(data);
        intent.putExtra(Constants.PLAYLIST_ACTIVITY_DATA_KEY, dataString);
        return intent;
    }
}