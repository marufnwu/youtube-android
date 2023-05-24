package com.logicline.tech.stube.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.databinding.ActivityPlayerBinding;
import com.logicline.tech.stube.models.HomeVideo;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";

    private ActivityPlayerBinding binding;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer mYouTubePlayer;
    private boolean isFullscreen = false;
    private HomeVideo.Item data;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Gson gson = new Gson();
        String gsonString = getIntent().getStringExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY);
        HomeVideo.Item data = gson.fromJson(gsonString, HomeVideo.Item.class);

        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        String videoUrl = "https://www.youtube.com/watch?v=CH-2WJmpdeQ";

        youTubePlayerView = binding.youtubePlayerView;
        FrameLayout fullscreenViewContainer = binding.fullScreenViewContainer;


        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1) // enable full screen button
                .build();

        youTubePlayerView.setEnableAutomaticInitialization(false);

        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {
                isFullscreen = true;
                Log.d(TAG, "onEnterFullscreen: is called");

                youTubePlayerView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(view);

                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }

            @Override
            public void onExitFullscreen() {
                isFullscreen = false;
                Log.d(TAG, "onExitFullscreen: is called ");

                // the video will continue playing in the player
                youTubePlayerView.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
            }
        });

        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                mYouTubePlayer = youTubePlayer;

                /*if (videoUrl != null){
                    String videoId = Utils.getYoutubeVideoIdFromUrl(videoUrl);
                    if (videoId == null){

                        Log.d(TAG, "onReady: video url " + videoUrl);
                        return;
                    }
                    Log.d(TAG, "onReady: videoid" + videoId);
                    youTubePlayer.loadVideo(videoId, 0);
                }*/
                //String videoId = UrlUtils.getYoutubeVideoIdFromUrl(videoUrl);
                String videoId = data.id;
                if (videoId != null) {
                    youTubePlayer.loadVideo(videoId, 0);
                }
            }
        }, iFramePlayerOptions);

        getLifecycle().addObserver(youTubePlayerView);


    }
}