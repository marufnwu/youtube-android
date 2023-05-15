package com.logicline.tech.stube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.logicline.tech.stube.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, Handler.Callback {

    private final String youtubeUrl = "https://www.youtube.com/";
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private final Handler handler = new Handler(this);

    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    @Override
    public void onBackPressed() {
        if (binding.wvYoutubeHome.canGoBack())
            binding.wvYoutubeHome.goBack();
        else
            super.onBackPressed();
    }

    private void initViews(){

        //init youtube webview
        WebView youtubeWebView = binding.wvYoutubeHome;
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // This line will be added
        youtubeWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        youtubeWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                binding.pbHomeLoading.setVisibility(View.GONE);
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        });
        youtubeWebView.loadUrl(youtubeUrl);

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == CLICK_ON_URL){
            handler.removeMessages(CLICK_ON_WEBVIEW);
            return true;
        }
        if (msg.what == CLICK_ON_WEBVIEW){
            Toast.makeText(this, "WebView clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.wv_youtube_home && event.getAction() == MotionEvent.ACTION_DOWN){
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
        }
        return false;
    }
}