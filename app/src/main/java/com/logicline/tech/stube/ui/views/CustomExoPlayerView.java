package com.logicline.tech.stube.ui.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.logicline.tech.stube.databinding.DoubleTapOverlayBinding;
import com.logicline.tech.stube.databinding.PlayerGestureControlsViewBinding;
import com.logicline.tech.stube.ui.activities.playerActivity.PlayerViewModel;
import com.logicline.tech.stube.ui.views.interfaces.OnlinePlayerOptions;
import com.logicline.tech.stube.ui.views.interfaces.PlayerGestureOptions;
import com.logicline.tech.stube.ui.views.interfaces.PlayerOptions;

public class CustomExoPlayerView extends PlayerView implements PlayerOptions, PlayerGestureOptions {
    private Activity  activity;
    private FragmentManager supportFragmentManager;
    private PlayerGestureControlsViewBinding playerGestureControlsViewBinding;
    private OnlinePlayerOptions playerOptionsInterface;
    private DoubleTapOverlayBinding doubleTapOverlayBinding;
    private PlayerGestureControlsViewBinding gestureViewBinding;
    private TrackSelector trackSelector;
    private PlayerViewModel playerViewModel;
    private LifecycleOwner viewLifecycleOwner;

    public CustomExoPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        activity = (Activity) context;

    }

    private void initialize(
            OnlinePlayerOptions playerViewInterface,
            DoubleTapOverlayBinding doubleTapOverlayBinding,
            PlayerGestureControlsViewBinding playerGestureControlsViewBinding,
            TrackSelector trackSelector,
            PlayerViewModel playerViewModel,
            LifecycleOwner viewLifecycleOwner
    ){
        this.playerGestureControlsViewBinding = playerGestureControlsViewBinding;
        this.playerOptionsInterface = playerViewInterface;
        this.doubleTapOverlayBinding = doubleTapOverlayBinding;
        this.trackSelector = trackSelector;
        this.gestureViewBinding = playerGestureControlsViewBinding;
        this.playerViewModel = playerViewModel;



    }

    @Override
    public void onSingleTap() {

    }

    @Override
    public void onDoubleTapCenterScreen() {

    }

    @Override
    public void onDoubleTapLeftScreen() {

    }

    @Override
    public void onDoubleTapRightScreen() {

    }

    @Override
    public void onSwipeLeftScreen(float distanceY) {

    }

    @Override
    public void onSwipeRightScreen(float distanceY) {

    }

    @Override
    public void onSwipeCenterScreen(float distanceY) {

    }

    @Override
    public void onSwipeEnd() {

    }

    @Override
    public void onZoom() {

    }

    @Override
    public void onMinimize() {

    }

    @Override
    public void onFullscreenChange(boolean isFullscreen) {

    }

    @Override
    public void onPlaybackSpeedClicked() {

    }

    @Override
    public void onResizeModeClicked() {

    }

    @Override
    public void onRepeatModeClicked() {

    }
}
