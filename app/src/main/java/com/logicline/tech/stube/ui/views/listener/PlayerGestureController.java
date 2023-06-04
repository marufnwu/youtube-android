package com.logicline.tech.stube.ui.views.listener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.logicline.tech.stube.ui.activities.playerActivity.PlayerViewModel;
import com.logicline.tech.stube.ui.views.helpers.PlayerHelper;
import com.logicline.tech.stube.ui.views.interfaces.PlayerGestureOptions;

public class PlayerGestureController implements View.OnTouchListener {
    private static final String SINGLE_TAP_TOKEN = "singleTap";
    private static final long MAX_TIME_DIFF = 400L;
    private static final int MOVEMENT_THRESHOLD = 30;
    private static final int BORDER_THRESHOLD = 90;
    private final Activity activity;
    private final PlayerGestureOptions listener;
    private final int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    private final int height = Resources.getSystem().getDisplayMetrics().heightPixels;
    private final int orientation = Resources.getSystem().getConfiguration().orientation;
    private final long elapsedTime = SystemClock.elapsedRealtime();
    private final PlayerViewModel playerViewModel;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final GestureDetector gestureDetector;
    private final ScaleGestureDetector scaleGestureDetector;
    private boolean isFullscreen = false;
    private boolean isMoving = false;
    private final boolean isEnabled = true;
    private boolean wasClick = true;
    private Context context;

    public PlayerGestureController(Activity activity, PlayerGestureOptions listener) {
        this.activity = activity;
        this.listener = listener;

        playerViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PlayerViewModel.class);

        gestureDetector = new GestureDetector(activity, new GestureListener(), handler);
        scaleGestureDetector = new ScaleGestureDetector(activity, new ScaleGestureListener(), handler);

        playerViewModel.isFullscreen().observe((LifecycleOwner) activity, isFullscreen -> {
            this.isFullscreen = isFullscreen;
            listener.onFullscreenChange(isFullscreen);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && isMoving) {
            isMoving = false;
            listener.onSwipeEnd();
        }

        if (event.getY() < height * 0.1 && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }

        try {
            scaleGestureDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
        } catch (Exception ignored) {
        }

        return isFullscreen;
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float scaleFactor = 1f;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            wasClick = false;
            scaleFactor *= detector.getScaleFactor();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (scaleFactor < 0.8) {
                listener.onMinimize();
            } else if (scaleFactor > 1.2) {
                listener.onZoom();
            }
            scaleFactor = 1f;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private long lastClick = 0L;
        private long lastDoubleClick = 0L;

        @Override
        public boolean onDown(MotionEvent e) {
            wasClick = true;

            if (isMoving || scaleGestureDetector.isInProgress()) {
                return false;
            }

            if (!PlayerHelper.doubleTapToSeek) {
                listener.onSingleTap();
                return true;
            }

            if (isEnabled && isSecondClick()) {
                handler.removeCallbacksAndMessages(SINGLE_TAP_TOKEN);
                lastDoubleClick = elapsedTime;
                float eventPositionPercentageX = e.getX() / width;

                if (eventPositionPercentageX < 0.4) {
                    listener.onDoubleTapLeftScreen();
                } else if (eventPositionPercentageX > 0.6) {
                    listener.onDoubleTapRightScreen();
                } else {
                    listener.onDoubleTapCenterScreen();
                }
            } else {
                if (recentDoubleClick()) {
                    return true;
                }
                handler.removeCallbacksAndMessages(SINGLE_TAP_TOKEN);
                handler.postDelayed(() -> {
                    if (!wasClick || isSecondClick()) {
                        return;
                    }
                    listener.onSingleTap();
                }, MAX_TIME_DIFF);
                lastClick = elapsedTime;
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isEnabled || scaleGestureDetector.isInProgress()) {
                return false;
            }

            boolean insideThreshHold = Math.abs(e2.getY() - e1.getY()) <= MOVEMENT_THRESHOLD;
            boolean insideBorder = (e1.getX() < BORDER_THRESHOLD || e1.getY() < BORDER_THRESHOLD ||
                    e1.getX() > width - BORDER_THRESHOLD || e1.getY() > height - BORDER_THRESHOLD);

            if (!isMoving && (insideThreshHold || insideBorder || Math.abs(distanceX) > Math.abs(distanceY))) {
                return false;
            }

            isMoving = true;
            wasClick = false;

            if (e1.getX() < width * 0.4) {
                listener.onSwipeLeftScreen(distanceY);
            } else if (e1.getX() > width * 0.6) {
                listener.onSwipeRightScreen(distanceY);
            } else {
                listener.onSwipeCenterScreen(distanceY);
            }
            return true;
        }

        private boolean isSecondClick() {
            return elapsedTime - lastClick < MAX_TIME_DIFF;
        }

        private boolean recentDoubleClick() {
            return elapsedTime - lastDoubleClick < MAX_TIME_DIFF / 2;
        }
    }
}

