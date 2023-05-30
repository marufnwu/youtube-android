package com.logicline.tech.stube.ui.fragments;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.logicline.tech.stube.api.ApiClient;
import com.logicline.tech.stube.api.VideoInterface;
import com.logicline.tech.stube.constants.ApiConstants;
import com.logicline.tech.stube.models.SearchItem;
import com.logicline.tech.stube.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends AndroidViewModel {
    private static final String TAG = "SearchViewModel";
    private final MutableLiveData<SearchItem> searchItemMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<SearchItem> searchItemNextPage = new MutableLiveData<>();
    private final VideoInterface videoInterface;
    private String nextPageToken;

    public SearchViewModel(@NonNull Application application) {
        super(application);

        videoInterface = ApiClient.getInstance(
                        ApiConstants.API_BASE_URL)
                .create(VideoInterface.class);
    }

    public MutableLiveData<SearchItem> getSearchResult() {
        return searchItemMutableLiveData;
    }

    public MutableLiveData<SearchItem> getSearchResultNextPage() {
        return searchItemNextPage;
    }

    public void search(String query) {
        Log.d(TAG, "search: is called");
        Call<SearchItem> searchItemCall = videoInterface.getSearchResult(
                ApiConstants.API_PART_SNIPPET, query, ApiConstants.API_KEY);

        //searchItemMutableLiveData = new MutableLiveData<>();

        searchItemCall.enqueue(new Callback<SearchItem>() {
            @Override
            public void onResponse(Call<SearchItem> call, Response<SearchItem> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: search post value");
                        searchItemMutableLiveData.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else {
                        Utils.showLongLogMsg("response", response.toString());
                        Log.d(TAG, "onResponse: search response body is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchItem> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                searchItemMutableLiveData.postValue(null);
            }
        });

    }

    public void nextPage(String query) {
        Call<SearchItem> searchItemCall = videoInterface.getSearchResultNextPage(
                ApiConstants.API_PART_SNIPPET, query, ApiConstants.API_KEY, nextPageToken);

        searchItemCall.enqueue(new Callback<SearchItem>() {
            @Override
            public void onResponse(Call<SearchItem> call, Response<SearchItem> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: search post value");
                        searchItemNextPage.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else {
                        Utils.showLongLogMsg("response", response.toString());
                        Log.d(TAG, "onResponse: search response body is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchItem> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                searchItemMutableLiveData.postValue(null);
            }
        });
    }

}
