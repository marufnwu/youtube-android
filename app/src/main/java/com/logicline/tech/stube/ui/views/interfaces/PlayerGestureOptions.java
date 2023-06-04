package com.logicline.tech.stube.ui.views.interfaces;

public interface PlayerGestureOptions {

    void onSingleTap();

    void onDoubleTapCenterScreen();

    void onDoubleTapLeftScreen();

    void onDoubleTapRightScreen();

    void onSwipeLeftScreen(float distanceY);

    void onSwipeRightScreen(float distanceY);

    void onSwipeCenterScreen(float distanceY);

    void onSwipeEnd();

    void onZoom();

    void onMinimize();

    void onFullscreenChange(boolean isFullscreen);
}
