package com.logicline.tech.stube.ui.activities.playerActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.databinding.ActivityPlayerBinding;
import com.logicline.tech.stube.models.CommentThread;
import com.logicline.tech.stube.models.RelatedVideo;
import com.logicline.tech.stube.models.VideoDetails;
import com.logicline.tech.stube.ui.activities.channelActivity.ChannelActivity;
import com.logicline.tech.stube.ui.adapters.RelatedVideoAdapter;
import com.logicline.tech.stube.ui.dialog.CommentBottomSheet;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;

import java.util.List;

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
    private CommentBottomSheet bottomSheetDialogFragment;
    private String videoId;

    /**
     * sent player activity intent for client functions
     *
     * @param context from which activity
     * @return player intent
     */
    public static Intent getPlayerActivityIntent(Context context, String videoId) {
        Intent playerActivityIntent = new Intent(context, PlayerActivity.class);
        playerActivityIntent.putExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY, videoId);
        return playerActivityIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add layout file and view binding
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        videoId = getIntent().getStringExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY);

        initViews();
    }

    private void initViews() {
        setupYoutubePlayer(videoId);

        //Related video list init
        adapter = new RelatedVideoAdapter(getApplicationContext());
        //adding onclick listener
        adapter.setItemClickListener(new RelatedVideoAdapter.ItemClickListener() {
            @Override
            public void onVideoClick(RelatedVideo.Item item) {
                adapter.clearData();
                mYouTubePlayer.loadVideo(item.id.videoId, 0);
                viewModel.loadRelatedVideos(item.id.videoId);
                videoId = item.id.videoId;
                viewModel.loadVideoDetails(videoId);
            }

            @Override
            public void onChannelClick(RelatedVideo.Item item) {
                Intent channelIntent = ChannelActivity.getChannelActivityIntent(getApplicationContext(), item.snippet.channelId);
                startActivity(channelIntent);
            }

        });

        binding.rvRelatedVideo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvRelatedVideo.setAdapter(adapter);

        //init viewModel
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        viewModel.loadRelatedVideos(videoId);
        viewModel.loadVideoDetails(videoId);

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
        binding.tvPlayerComment.setOnClickListener(this);
        binding.ivPlayerComment.setOnClickListener(this);
        binding.tvPlayerShare.setOnClickListener(this);
        binding.ivPlayerShare.setOnClickListener(this);
        binding.ivPlayerChannelAvatar.setOnClickListener(this);
        binding.tvPlayerChannelName.setOnClickListener(this);

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

        viewModel.getVideoDetails().observe(this, new Observer<VideoDetails>() {
            @Override
            public void onChanged(VideoDetails videoDetails) {
                if (videoDetails == null || videoDetails.items == null)
                    return;
                if (videoDetails.items.size() > 0){
                VideoDetails.Item item = videoDetails.items.get(0);
                binding.tvPlayerLike.setText(item.statistics.likeCount);
                binding.tvPlayerComment.setText(item.statistics.commentCount);
                binding.tvPlayerVideoView.setText(item.statistics.viewCount + " views");

                binding.tvPlayerVideoTitle.setText(item.snippet.title);
                binding.tvPlayerVideoDescription.setText(item.snippet.description);
                binding.tvPlayerChannelName.setText(item.snippet.channelTitle);
                }
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
        } else if (binding.tvPlayerComment.equals(v) || binding.ivPlayerComment.equals(v)) {
            Toast.makeText(getApplicationContext(), "comments", Toast.LENGTH_SHORT).show();

            bottomSheetDialogFragment = new CommentBottomSheet();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BOTTOM_SHEET_DATA_KEY, videoId);
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "bottomSheet");

        } else if (binding.tvPlayerShare.equals(v) || binding.ivPlayerShare.equals(v)) {
            Toast.makeText(getApplicationContext(), "share", Toast.LENGTH_SHORT).show();

            String link = "https://www.youtube.com/watch?v=" + videoId;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, link);

            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            if (!activities.isEmpty()) {
                startActivity(Intent.createChooser(intent, "Share Link"));
            }
        } else if (binding.ivPlayerChannelAvatar.equals(v) || binding.tvPlayerChannelName.equals(v)) {
            //ChannelActivity.getChannelActivityIntent(getApplicationContext(), )
            Toast.makeText(this, "channel clicked", Toast.LENGTH_SHORT).show();
        }
    }
}