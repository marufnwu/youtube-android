package com.logicline.tech.stube.ui.activities.playerActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.databinding.ActivityPlayerBinding;
import com.logicline.tech.stube.models.PlayerData;
import com.logicline.tech.stube.models.RelatedVideo;
import com.logicline.tech.stube.ui.adapters.RelatedVideoAdapter;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PlayerActivity";

    private ActivityPlayerBinding binding;
    private YouTubePlayer mYouTubePlayer;
    private RelatedVideoAdapter adapter;
    private PlayerViewModel viewModel;
    private boolean isFullscreen = false;
    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (isFullscreen) {
                mYouTubePlayer.toggleFullscreen();
            } else {
                finish();
            }
        }
    };
    private PlayerData intentData;

    /**
     * sent player activity intent for client functions
     *
     * @param context    from which activity
     * @param playerData data for player activity
     * @return player intent
     */
    public static Intent getPlayerActivityIntent(Context context, PlayerData playerData) {
        Intent playerActivityIntent = new Intent(context, PlayerActivity.class);
        String data = new Gson().toJson(playerData);
        playerActivityIntent.putExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY, data);
        return playerActivityIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add layout file and view binding
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //getting
        Gson gson = new Gson();
        String gsonString = getIntent().getStringExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY);
        intentData = gson.fromJson(gsonString, PlayerData.class);

        initViews();
    }

    private void initViews() {
        setupYoutubePlayer(intentData.getVideoId());

        binding.tvPlayerVideoTitle.setText(intentData.getTitle());
        binding.tvPlayerVideoDescription.setText(intentData.getDescription());

        //Related video list init
        adapter = new RelatedVideoAdapter(getApplicationContext());
        //adding onclick listener
        adapter.setItemClickListener(new RelatedVideoAdapter.ItemClickListener() {
            @Override
            public void onClick(RelatedVideo.Item item) {
                            /*adapter.clearData();
                            //setupYoutubePlayer(item.id.videoId);
                            viewModel.getRelatedVideos(item.id.videoId);*/

                            /*Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                            String myGson = new Gson().toJson(item);
                            intent.putExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY, myGson);
                            startActivity(intent);*/

                adapter.clearData();
                mYouTubePlayer.loadVideo(item.id.videoId, 0);
                viewModel.loadRelatedVideos(item.id.videoId);

            }
        });

        binding.rvRelatedVideo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvRelatedVideo.setAdapter(adapter);

        //init viewModel
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        viewModel.loadRelatedVideos(intentData.getVideoId());

        /* notify nested scrollView at the end position
        binding.nsvPlayerScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // End of NestedScrollView reached
                    // Perform your desired actions
                    Log.d(TAG, "onScrollChange: lastaaa");
                    Toast.makeText(getApplicationContext(), "Lastaaaa", Toast.LENGTH_SHORT).show();

                    getNextPage();
                }
            }
        });*/

        binding.tvPlayerVideoTitle.setOnClickListener(this);

        initViewModelObserver();
    }

    private void initViewModelObserver() {
        viewModel.getRelatedVideo().observe(this, new Observer<RelatedVideo>() {
            @Override
            public void onChanged(RelatedVideo relatedVideo) {
                if (relatedVideo == null || relatedVideo.items == null)
                    return;

                adapter.setData(relatedVideo.items);

                binding.pbPlayerRecentVideos.setVisibility(View.GONE);
            }
        });
    }

    /* Get next page of related video from make api call
    private void getNextPage(){
        MutableLiveData<RelatedVideo> videos = viewModel.getRelatedVideoNextPage(intentData.id);
        if (videos != null) {
            videos.observe(this, new Observer<RelatedVideo>() {
                @Override
                public void onChanged(RelatedVideo relatedVideo) {
                    if (relatedVideo == null || relatedVideo.items == null)
                        return;

                    //add new videos to the recyclerView
                    adapter.addData(relatedVideo.items);
                }
            });
        }else {
            Log.d(TAG, "initViews: api response null");
        }
    }*/

    private void setupYoutubePlayer(String videoId) {
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1) // enable full screen button
                .build();

        binding.youtubePlayerView.setEnableAutomaticInitialization(false);
        binding.youtubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {
                isFullscreen = true;
                Log.d(TAG, "onEnterFullscreen: is called");

                binding.youtubePlayerView.setVisibility(View.GONE);
                binding.fullScreenViewContainer.setVisibility(View.VISIBLE);
                binding.fullScreenViewContainer.addView(view);

                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }

            @Override
            public void onExitFullscreen() {
                isFullscreen = false;
                Log.d(TAG, "onExitFullscreen: is called ");

                // the video will continue playing in the player
                binding.youtubePlayerView.setVisibility(View.VISIBLE);
                binding.fullScreenViewContainer.setVisibility(View.GONE);
                binding.fullScreenViewContainer.removeAllViews();
            }
        });
        binding.youtubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                mYouTubePlayer = youTubePlayer;

                if (videoId != null) {
                    youTubePlayer.loadVideo(videoId, 0);
                }
            }
        }, iFramePlayerOptions);


        getLifecycle().addObserver(binding.youtubePlayerView);
    }

    @Override
    public void onClick(View v) {
        //int id = v.getId();
        if (binding.tvPlayerVideoTitle.equals(v)) {
            if (binding.tvPlayerVideoDescription.isShown()) {
                binding.tvPlayerVideoDescription.setVisibility(View.GONE);
                binding.tvPlayerVideoTitle.setMaxLines(2);
            } else {
                binding.tvPlayerVideoDescription.setVisibility(View.VISIBLE);
                binding.tvPlayerVideoTitle.setMaxLines(10);
                if (binding.pbPlayerRecentVideos.isShown())
                    binding.pbPlayerRecentVideos.setVisibility(View.GONE);
            }
        }
    }
}