package com.logicline.tech.stube.ui.activities.MainActivity;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.Shimmer;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.databinding.ActivityMainBinding;
import com.logicline.tech.stube.models.HomeVideo;
import com.logicline.tech.stube.models.PlayerData;
import com.logicline.tech.stube.ui.activities.channelActivity.ChannelActivity;
import com.logicline.tech.stube.ui.activities.playerActivity.PlayerActivity;
import com.logicline.tech.stube.ui.adapters.VideoItemAdapter;
import com.logicline.tech.stube.ui.fragments.SearchFragment;
import com.logicline.tech.stube.utils.ConnectionUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final String IS_SEARCH_EXPAND_KEY = "isSearchExpand";
    private final String IS_SEARCH_RESULT_FRAGMENT_SHOWING_KEY = "isFragmentShowing";
    private final String QUERY_KEY = "query";
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private VideoItemAdapter adapter;
    private boolean isLoading = false;
    private boolean isSearchViewExpanded = false;
    private boolean isSearchResultFragmentIsShowing = false;
    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(QUERY_KEY);
            isSearchViewExpanded = savedInstanceState.getBoolean(IS_SEARCH_EXPAND_KEY);
            isSearchResultFragmentIsShowing = savedInstanceState.getBoolean(IS_SEARCH_RESULT_FRAGMENT_SHOWING_KEY);
        }

        //set layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Check the internet connection
        boolean isConnected = checkInternetConnection();
        if (!isConnected)
            return;

        //Initialize views
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
        if (!binding.rvHomeVideos.isShown()) {
            binding.rvHomeVideos.setVisibility(View.VISIBLE);
            binding.fragmentContainer.setVisibility(View.GONE);

            isSearchResultFragmentIsShowing = false;
        } else
            super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(QUERY_KEY, mQuery);
        outState.putBoolean(IS_SEARCH_EXPAND_KEY, isSearchViewExpanded);
        outState.putBoolean(IS_SEARCH_RESULT_FRAGMENT_SHOWING_KEY, isSearchResultFragmentIsShowing);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        //Init viewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if (!isSearchViewExpanded) {
            binding.tvLogoText.setVisibility(View.VISIBLE);
        } else
            binding.tvLogoText.setVisibility(View.INVISIBLE);
        if (isSearchResultFragmentIsShowing) {
            binding.rvHomeVideos.setVisibility(View.GONE);
            openSearchFragment();
        }

        binding.rvHomeVideos.setLayoutManager(new LinearLayoutManager(this));

        //Create adapter for recyclerview
        adapter = new VideoItemAdapter(getApplicationContext());

        //Handle item click
        adapter.setItemClickListener(new VideoItemAdapter.ItemClickListener() {
            @Override
            public void onVideoClick(HomeVideo.Item item) {
                PlayerData playerData = new PlayerData(item.snippet.title,
                        item.snippet.description, item.id);
                Intent playerIntent = PlayerActivity
                        .getPlayerActivityIntent(MainActivity.this, playerData);
                startActivity(playerIntent);
            }

            @Override
            public void onChannelClick(HomeVideo.Item item) {
                Intent channelIntent = ChannelActivity.getChannelActivityIntent(getApplicationContext(), item.snippet.channelId);
                startActivity(channelIntent);
            }

           /* @Override
            public void onClick(HomeVideo.Item item) {

            }*/
        });

        binding.rvHomeVideos.setAdapter(adapter);
        binding.rvHomeVideos.setVisibility(View.VISIBLE);

        initViewModelObserver();

        //Find end of the recyclerview and load next page
        findEndOfRecyclerViewAndLoadNextPage();

        //Handle search view open and close
        handleSearchViewState();

        //Handle search Query
        handleSearch();

    }

    private void initViewModelObserver() {
        viewModel.getHomeVideos().observe(this, new Observer<HomeVideo>() {
            @Override
            public void onChanged(HomeVideo homeVideo) {
                if (homeVideo != null) {
                    adapter.setData(homeVideo.items);

                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getNextPage().observe(this, new Observer<HomeVideo>() {
            @Override
            public void onChanged(HomeVideo homeVideo) {
                if (homeVideo != null) {
                    adapter.addData(homeVideo.items);
                    isLoading = false;
                    Log.d(TAG, "getNextPage: loading finished");
                }
            }
        });
    }

    /**
     * This function find the end of the home videos recyclerView
     * then call load next page.
     */
    private void findEndOfRecyclerViewAndLoadNextPage() {
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

    /**
     * Get next page data from network call in view model
     */
    private void getNextPage() {
        Log.d(TAG, "getNextPage: is called");
        viewModel.getHomeVideoNextPage();

    }

    /**
     * Handle if searchView is iconified or expanded
     */
    private void handleSearchViewState() {
        binding.svHomeSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SearchView is expanded
                binding.tvLogoText.setVisibility(View.INVISIBLE);
                isSearchViewExpanded = true;
            }
        });
        binding.svHomeSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //searchview is not expanded
                binding.tvLogoText.setVisibility(View.VISIBLE);
                isSearchViewExpanded = false;
                return false;
            }
        });
    }

    /**
     * Handle query on searchView and pass the query to the search result fragment
     */
    private void handleSearch() {
        binding.svHomeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "search onQueryTextSubmit: is called");
                mQuery = query;
                binding.svHomeSearch.clearFocus();

                SearchFragment fragment = new SearchFragment(mQuery);
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fragmentContainer.getId(), fragment)
                        .commit();
                binding.fragmentContainer.setVisibility(View.VISIBLE);
                binding.rvHomeVideos.setVisibility(View.GONE);

                isSearchResultFragmentIsShowing = true;

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void openSearchFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentContainer.getId(), new SearchFragment())
                .commit();
        binding.fragmentContainer.setVisibility(View.VISIBLE);
        binding.rvHomeVideos.setVisibility(View.GONE);

        isSearchResultFragmentIsShowing = true;
    }

    /**
     * This function check internet connected or not
     * if internet is not connected then show no internet dialog
     *
     * @return true if internet connected else false
     */
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