package com.logicline.tech.stube.ui.activities.MainActivity;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.Shimmer;
import com.google.gson.Gson;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.databinding.ActivityMainBinding;
import com.logicline.tech.stube.models.HomeVideo;
import com.logicline.tech.stube.ui.activities.playerActivity.PlayerActivity;
import com.logicline.tech.stube.ui.adapters.VideoItemAdapter;
import com.logicline.tech.stube.utils.ConnectionUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private VideoItemAdapter adapter;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Check the internet connection
        boolean isConnected = checkInternetConnection();
        if (!isConnected)
            return;

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.shimmerViewContainer.startShimmer();

        Shimmer.AlphaHighlightBuilder shimmerBuilder = new Shimmer.AlphaHighlightBuilder();
        shimmerBuilder.setDuration(5000L).setRepeatMode(ValueAnimator.REVERSE);
        shimmerBuilder.build();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViews() {
        binding.rvHomeVideos.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        MutableLiveData<HomeVideo> homeVideoLiveData = viewModel.getHomeVideos();
        if (homeVideoLiveData != null) {
            homeVideoLiveData.observe(this, homeVideo -> {
                if (homeVideo == null || homeVideo.items == null)
                    return;
                adapter = new VideoItemAdapter(getApplicationContext(), homeVideo.items);

                //Handle item click
                adapter.setItemClickListener(new VideoItemAdapter.ItemClickListener() {
                    @Override
                    public void onClick(HomeVideo.Item item) {
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(item);
                        intent.putExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY, myGson);
                        startActivity(intent);
                    }
                });

                binding.rvHomeVideos.setAdapter(adapter);
                binding.rvHomeVideos.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
            });
        } else
            Log.d(TAG, "onChanged: home video livedata is null");

        binding.rvHomeVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.d(TAG, "onScrolled: dy " + dy);
                if (dy > 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager == null)
                        return;
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    Log.d(TAG, "onScrolled: total item count " + totalItemCount);
                    Log.d(TAG, "onScrolled: visible item count " + visibleItemCount);
                    Log.d(TAG, "onScrolled: first visible item position " + firstVisibleItemPosition);

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        // End of RecyclerView reached
                        Log.d(TAG, "onScrolled: last");

                        if (!isLoading) {
                            getNextPage();
                            isLoading = true;
                        }

                    }
                }
            }
        });
    }

    private void getNextPage() {
        Log.d(TAG, "getNextPage: is called");
        MutableLiveData<HomeVideo> homeVideoNextPageLiveData = viewModel.getHomeVideoNextPage();

        if (homeVideoNextPageLiveData == null)
            return;
        homeVideoNextPageLiveData.observe(this, new Observer<HomeVideo>() {
            @Override
            public void onChanged(HomeVideo homeVideo) {
                adapter.addData(homeVideo.items);
                isLoading = false;
                Log.d(TAG, "getNextPage: loading finished");
            }
        });
    }

    private boolean checkInternetConnection() {
        if (!ConnectionUtils.isInternetConnected(this)) {
            Dialog noInternetDialog = new Dialog(this);
            noInternetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            noInternetDialog.setContentView(R.layout.dialog_no_internet);
            noInternetDialog.setCancelable(false);

            noInternetDialog.show();
            binding.shimmerViewContainer.setVisibility(View.GONE);
            return false;
        }
        return true;
    }
}