package com.logicline.tech.stube.ui.activities.channelActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.databinding.ActivityChannelBinding;
import com.logicline.tech.stube.models.ChannelData;
import com.logicline.tech.stube.models.ChannelVideo;
import com.logicline.tech.stube.models.PlayerData;
import com.logicline.tech.stube.ui.activities.playerActivity.PlayerActivity;
import com.logicline.tech.stube.ui.adapters.ChannelVideoAdapter;

public class ChannelActivity extends AppCompatActivity {
    private static final String TAG = "ChannelActivity";
    private ChannelData intentData;
    private ActivityChannelBinding binding;
    private ChannelViewModel viewModel;
    private ChannelVideoAdapter adapter;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null) {
            String dataString = getIntent().getStringExtra(Constants.CHANNEL_ACTIVITY_INTENT_DATA_KEY);
            intentData = new Gson().fromJson(dataString, ChannelData.class);
        }

        init();

        Log.d(TAG, "onCreate: id: " + intentData.getChannelId() + "\ntitle: " + intentData.getTitle() +
                "Description: " + intentData.getDescription() + "\nUrl: " + intentData.getThumbnail());
    }

    private void init() {
        binding.tvChannelATitle.setText(intentData.getTitle());
        binding.tvChannelADescription.setText(intentData.getDescription());
        binding.tvChannelASubscriber.setText("17M Subscriber");
        Glide.with(this).load(intentData.getThumbnail()).into(binding.ivChannelAThumbnail);

        binding.rvChannelAVideos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChannelVideoAdapter(this);
        adapter.addItemClickListener(new ChannelVideoAdapter.ItemClickListener() {
            @Override
            public void onclick(ChannelVideo.Item item) {
                PlayerData playerData = new PlayerData(item.snippet.title,
                        item.snippet.description, item.id.videoId);

                Intent intent = PlayerActivity.getPlayerActivityIntent(getApplicationContext(), playerData);
                startActivity(intent);
            }
        });
        binding.rvChannelAVideos.setAdapter(adapter);

        //Init view models
        viewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        viewModel.getChannelVideoItems(intentData.getChannelId());

        initViewModelObserver();
        findEndOfRecyclerViewAndLoadNextPage();

    }

    private void initViewModelObserver() {

        viewModel.getNextPage().observe(this, new Observer<ChannelVideo>() {
            @Override
            public void onChanged(ChannelVideo channelVideo) {
                if (channelVideo.items != null) {
                    adapter.addData(channelVideo.items);
                    isLoading = false;
                    Log.d(TAG, "getNextPage: loading finished " + channelVideo.items.size());
                }
            }
        });
        viewModel.getChannelVideo().observe(this, new Observer<ChannelVideo>() {
            @Override
            public void onChanged(ChannelVideo channelVideo) {
                if (channelVideo != null && channelVideo.items != null) {
                    if (adapter != null)
                        adapter.setData(channelVideo.items);
                }
            }
        });
    }

    /**
     * This function find the end of the home videos recyclerView
     * then call load next page.
     */
    private void findEndOfRecyclerViewAndLoadNextPage() {
        binding.rvChannelAVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                        Log.d("ChannelListLog", "end of page hit");

                        if (!isLoading) {
                            getNextPage();
                            isLoading = true;
                        } else {
                            Log.d("ChannelListLog", "Another page laoding");

                        }
                    }
                }
            }
        });
    }

    /**
     * Get next page data from network call in view model
     */
    private void getNextPage() {
        Log.d(TAG, "getNextPage: is called");
        Log.d("ChannelListLog", "Next Page Called");

        viewModel.getNextPage(intentData.getChannelId());
    }
}