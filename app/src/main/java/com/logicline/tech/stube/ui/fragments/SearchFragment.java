package com.logicline.tech.stube.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.logicline.tech.stube.R;
import com.logicline.tech.stube.models.SearchItem;
import com.logicline.tech.stube.ui.activities.channelActivity.ChannelActivity;
import com.logicline.tech.stube.ui.activities.playerActivity.PlayerActivity;
import com.logicline.tech.stube.ui.adapters.SearchItemAdapter;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    SearchViewModel viewModel;
    private SearchItemAdapter adapter;
    private ProgressBar loadingPb;
    private RecyclerView searchResult;
    private String query;
    private boolean isLoading = false;

    public SearchFragment() {
        // Required empty public constructor
    }

    public SearchFragment(String query) {
        this.query = query;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (viewModel == null)
            viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        if (query != null) {
            viewModel.search(query);
        }
        initViewModelObservers();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchResult = view.findViewById(R.id.rv_searchResult);
        loadingPb = view.findViewById(R.id.pb_search_loading);

        adapter = new SearchItemAdapter(getContext());
        searchResult.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setItemClickListener(new SearchItemAdapter.ItemClickListener() {
            @Override
            public void onClickVideo(SearchItem.Item item) {
                /*Intent intent = new Intent(getContext(), PlayerActivity.class);
                String object = new Gson().toJson(item);
                intent.putExtra(Constants.PLAYER_ACTIVITY_INTENT_ITEM_KEY, object);
                startActivity(intent);*/

                Toast.makeText(getActivity(), "video clicked", Toast.LENGTH_SHORT).show();
                Intent videoIntent = PlayerActivity
                        .getPlayerActivityIntent(getActivity(), item.id.videoId);
                startActivity(videoIntent);
            }

            @Override
            public void onClickPlaylist(SearchItem.Item item) {

                Toast.makeText(getActivity(), "playlist clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickChannel(SearchItem.Item item) {

                Log.d(TAG, "onClickChannel: is called");
                Toast.makeText(getActivity(), "Channel clicked", Toast.LENGTH_SHORT).show();

                Intent intent = ChannelActivity.getChannelActivityIntent(getActivity(), item.id.channelId);
                startActivity(intent);
            }
        });

        searchResult.setAdapter(adapter);

        findEndOfRecyclerView();

        Log.d(TAG, "search onCreateView: is called");

        return view;
    }

    private void initViewModelObservers() {
        MutableLiveData<SearchItem> data = viewModel.getSearchResult();
        if (data != null) {
            Log.d(TAG, "onCreateView: search data is not null ");
            data.observe(this, new Observer<SearchItem>() {
                @Override
                public void onChanged(SearchItem searchItem) {
                    if (searchItem != null) {
                        Log.d(TAG, "onChanged: search items size " + searchItem.items.size());
                        adapter.setData(searchItem.items);
                        loadingPb.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            Log.d(TAG, "search onCreateView: data is null");
        }

        viewModel.getSearchResultNextPage().observe(this, new Observer<SearchItem>() {
            @Override
            public void onChanged(SearchItem searchItem) {
                if (searchItem != null) {
                    adapter.addData(searchItem.items);
                    isLoading = false;
                }
            }
        });
    }

    private void findEndOfRecyclerView() {
        searchResult.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                            viewModel.nextPage(query);
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }
}