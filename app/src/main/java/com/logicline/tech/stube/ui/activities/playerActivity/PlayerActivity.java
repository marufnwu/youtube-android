package com.logicline.tech.stube.ui.activities.playerActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.databinding.ActivityPlayerBinding;
import com.logicline.tech.stube.models.PlayerPlayListItem;
import com.logicline.tech.stube.models.RelatedVideo;
import com.logicline.tech.stube.models.VideoDetails;
import com.logicline.tech.stube.ui.activities.channelActivity.ChannelActivity;
import com.logicline.tech.stube.ui.adapters.RelatedVideoAdapter;
import com.logicline.tech.stube.ui.dialog.CommentBottomSheet;
import com.logicline.tech.stube.ui.dialog.PlayerPlayListBottomSheet;
import com.logicline.tech.stube.utils.Utils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;

import java.util.ArrayList;
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
    private ArrayList<RelatedVideo.Item> relatedVideos;
    private ArrayList<PlayerPlayListItem> playListItems = new ArrayList<>();
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
    private PlayerPlayListBottomSheet playListBottomSheet;
    private String videoId;
    private String channelId;
    private int playingVideoPosition = 0;

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

        if (getIntent().hasExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY))
            videoId = getIntent().getStringExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY);

        if (getIntent().hasExtra(Constants.PLAYER_ACTIVITY_INTENT_PLAYLIST)){
            ArrayList<String> dataString = getIntent().getStringArrayListExtra(Constants.PLAYER_ACTIVITY_INTENT_PLAYLIST);

            for (int i = 0; i< dataString.size(); i++){
                PlayerPlayListItem item = new Gson().fromJson(dataString.get(i), PlayerPlayListItem.class);
                item.setPosition(i);

                playListItems.add(item);
            }
            videoId = playListItems.get(0).getVideoId();
            binding.cvPlayerPlaylist.setVisibility(View.VISIBLE);
            binding.tvPlayerPlaylistNextVideoTitle.setText(playListItems.get(1).getVideoTitle());
            //binding.tvPlayerPlaylistName.setText(playListItems.get(0).);
            Log.d(TAG, "onCreate: videoId " + videoId);
        }
        if (getIntent().hasExtra(Constants.PLAYER_ACTIVITY_INTENT_PLAYLIST_NAME)){
            String playListName = getIntent().getStringExtra(Constants.PLAYER_ACTIVITY_INTENT_PLAYLIST_NAME);
            binding.tvPlayerPlaylistName.setText(playListName);
        }

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
                videoId = item.id.videoId;
                binding.cvPlayerPlaylist.setVisibility(View.GONE);
                loadNewVideo(videoId);
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

        if (videoId != null){
            viewModel.loadRelatedVideos(videoId);
            viewModel.loadVideoDetails(videoId);
        }


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
        binding.cvPlayerPlaylist.setOnClickListener(this);

        initViewModelObserver();
    }

    private void initViewModelObserver() {
        viewModel.getRelatedVideo().observe(this, new Observer<RelatedVideo>() {
            @Override
            public void onChanged(RelatedVideo relatedVideo) {
                if (relatedVideo == null || relatedVideo.error != null)
                    return;
                relatedVideos = relatedVideo.items;

                adapter.setData(relatedVideos);

                binding.pbPlayerRecentVideos.setVisibility(View.GONE);
            }
        });

        viewModel.getVideoDetails().observe(this, new Observer<VideoDetails>() {
            @Override
            public void onChanged(VideoDetails videoDetails) {
                if (videoDetails == null || videoDetails.error != null)
                    return;
                if (videoDetails.items.size() > 0){
                VideoDetails.Item item = videoDetails.items.get(0);
                String likeCount = Utils.shortenNumber(Double.parseDouble(item.statistics.likeCount));
                String commentCount = Utils.shortenNumber(Double.parseDouble(item.statistics.commentCount));
                String viewCount = Utils.shortenNumber(Double.parseDouble(item.statistics.viewCount));

                binding.tvPlayerLike.setText(likeCount);
                binding.tvPlayerComment.setText(commentCount);
                binding.tvPlayerVideoView.setText(viewCount + " views");

                binding.tvPlayerVideoTitle.setText(item.snippet.title);
                binding.tvPlayerVideoDescription.setText(item.snippet.description);
                binding.tvPlayerChannelName.setText(item.snippet.channelTitle);

                channelId = item.snippet.channelId;
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

        binding.youtubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {

            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState playerState) {
                if (playerState == PlayerConstants.PlayerState.PLAYING){
                    //the payer playing the video
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                if (playerState == PlayerConstants.PlayerState.PAUSED){
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

                if (playerState == PlayerConstants.PlayerState.ENDED){
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    new CountDownTimer(5000, 1000) {
                        long countDownTime = Long.MAX_VALUE;

                        public void onTick(long millisUntilFinished) {
                            binding.tvCountDown.setVisibility(View.VISIBLE);
                            long time = millisUntilFinished / 1000;
                            if (time < countDownTime){
                                countDownTime = time;
                                binding.tvCountDown
                                        .setText("Next video play in: " + time + " sec.");
                            }
                        }
                        public void onFinish() {
                            binding.tvCountDown.setVisibility(View.GONE);
                            if (relatedVideos != null){

                                if (playListItems != null && playListItems.size() != 0
                                        && playingVideoPosition < playListItems.size() - 1){
                                    Log.d(TAG, "onFinish: playing position " + playingVideoPosition);

                                    PlayerPlayListItem item = playListItems.get(playingVideoPosition + 1);
                                    loadNextPlaylistVideo(item);
                                }
                                else {
                                    binding.tvCountDown.setVisibility(View.GONE);
                                    binding.cvPlayerPlaylist.setVisibility(View.GONE);
                                    String nextVideoId = relatedVideos.get(0).id.videoId;
                                    loadNewVideo(nextVideoId);
                                }
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onPlaybackQualityChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError playerError) {

            }

            @Override
            public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoLoadedFraction(@NonNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(@NonNull YouTubePlayer youTubePlayer, @NonNull String s) {

            }

            @Override
            public void onApiChange(@NonNull YouTubePlayer youTubePlayer) {

            }
        });
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1) // enable full screen button
                .build();

        Thread backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

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
                    }
                });
                Looper.loop();
            }
        });

        backgroundThread.start();
        getLifecycle().addObserver(binding.youtubePlayerView);

    }

    private void loadNewVideo(String videoId){
        adapter.clearData();
        mYouTubePlayer.loadVideo(videoId, 0);
        viewModel.loadRelatedVideos(videoId);
        viewModel.loadVideoDetails(videoId);
    }

    private void loadNextPlaylistVideo(PlayerPlayListItem item){
        Log.d(TAG, "loadNextPlaylistVideo: is called");
        Log.d(TAG, "loadNextPlaylistVideo: item position " + item.getPosition());
        if (item.getPosition() != -1 && item.getPosition() + 1 < playListItems.size())
            binding.tvPlayerPlaylistNextVideoTitle.setText(
                    playListItems.get(item.getPosition() + 1).getVideoTitle());
        if (item.getPosition() == playListItems.size() - 1){
            binding.tvNextPlayerPlaylist.setText(R.string.end_of_the_playlist);
            binding.tvPlayerPlaylistNextVideoTitle.setVisibility(View.GONE);
        }
        playingVideoPosition = item.getPosition();
        Log.d(TAG, "loadNextPlaylistVideo: playing position " + playingVideoPosition);

        loadNewVideo(item.getVideoId());
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
            bundle.putString(Constants.COMMENT_BOTTOM_SHEET_DATA_KEY, videoId);
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
            if (channelId != null){
                Intent channelIntent = ChannelActivity.getChannelActivityIntent(getApplicationContext(), channelId);
                startActivity(channelIntent);
            }else {
                Log.d(TAG, "onClick: channel id is null");

            }

        } else if (binding.cvPlayerPlaylist.equals(v)) {
            playListBottomSheet = new PlayerPlayListBottomSheet();
            Bundle bundle = new Bundle();

            ArrayList<String> data = new ArrayList<>();
            for (PlayerPlayListItem item: playListItems){
                String itemString = new Gson().toJson(item);
                data.add(itemString);
            }

            bundle.putStringArrayList(Constants.PLAYLIST_BOTTOM_SHEET_DATA_KEY, data);
            playListBottomSheet.setArguments(bundle);

            playListBottomSheet.setPlayListVideoClickListener(new PlayerPlayListBottomSheet.PlaylistVideoClickListener() {
                @Override
                public void onClick(PlayerPlayListItem item) {
                    loadNextPlaylistVideo(item);
                }
            });
            playListBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        }
    }
}