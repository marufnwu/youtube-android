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

import com.google.gson.Gson;
import com.logicline.tech.stube.R;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.models.ChannelData;
import com.logicline.tech.stube.models.PlayerData;
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
                PlayerData playerData = new PlayerData(item.snippet.title,
                        item.snippet.description, item.id.videoId);
                Intent videoIntent = PlayerActivity
                        .getPlayerActivityIntent(getActivity(), playerData);
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

                ChannelData data = new ChannelData(item.id.channelId, item.snippet.title,
                        item.snippet.description, item.snippet.thumbnails.high.url);
                Intent intent = new Intent(getContext(), ChannelActivity.class);
                String dataString = new Gson().toJson(data);
                intent.putExtra(Constants.CHANNEL_ACTIVITY_INTENT_DATA_KEY, dataString);
                startActivity(intent);
            }
        });

        searchResult.setAdapter(adapter);

        Log.d(TAG, "search onCreateView: is called");

        MutableLiveData<SearchItem> data = viewModel.getSearchResult();
        if (data != null) {
            Log.d(TAG, "onCreateView: search data is not null ");
            data.observe(getViewLifecycleOwner(), new Observer<SearchItem>() {
                @Override
                public void onChanged(SearchItem searchItem) {
                    Log.d(TAG, "onChanged: search items size " + searchItem.items.size());
                    adapter.setData(searchItem.items);
                    loadingPb.setVisibility(View.GONE);
                }
            });
        } else {
            Log.d(TAG, "search onCreateView: data is null");
        }
        return view;
    }
}