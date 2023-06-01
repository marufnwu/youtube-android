package com.logicline.tech.stube.ui.activities.playlistActivity;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.logicline.tech.stube.api.ApiClient;
import com.logicline.tech.stube.api.VideoInterface;
import com.logicline.tech.stube.constants.ApiConstants;
import com.logicline.tech.stube.models.PlayListVideo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistViewModel extends AndroidViewModel {
    private static final String TAG = "PlaylistViewModel";
    private final VideoInterface videoInterface;
    private final MutableLiveData<PlayListVideo> playListVideo = new MutableLiveData<>();
    private final MutableLiveData<PlayListVideo> playListVideoNextPage = new MutableLiveData<>();
    private String nextPageToken;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);

        videoInterface = ApiClient.getInstance(ApiConstants.API_BASE_URL)
                .create(VideoInterface.class);
    }

    public MutableLiveData<PlayListVideo> getPlayListVideo() {
        return playListVideo;
    }

    public MutableLiveData<PlayListVideo> getPlayListVideoNextPage() {
        return playListVideoNextPage;
    }

    public void loadPlayListVideo(String playListId) {
        Call<PlayListVideo> videos = videoInterface.getPlayListVideo(ApiConstants.API_PART_SNIPPET,
                playListId, ApiConstants.API_KEY);

        videos.enqueue(new Callback<PlayListVideo>() {
            @Override
            public void onResponse(Call<PlayListVideo> call, Response<PlayListVideo> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        playListVideo.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else {
                        Log.d(TAG, "onResponse: response body is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<PlayListVideo> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                Log.d(TAG, "onFailure: " + t);
                playListVideo.postValue(null);
            }
        });
    }

    public void loadNextPage(String playlistId) {
        if (nextPageToken == null)
            return;

        Call<PlayListVideo> videos = videoInterface.getPlayListVideoNextPage(ApiConstants.API_PART_SNIPPET,
                playlistId, ApiConstants.API_KEY, nextPageToken);

        videos.enqueue(new Callback<PlayListVideo>() {
            @Override
            public void onResponse(Call<PlayListVideo> call, Response<PlayListVideo> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        playListVideoNextPage.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else {
                        Log.d(TAG, "onResponse: response body is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<PlayListVideo> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                Log.d(TAG, "onFailure: " + t);
                playListVideoNextPage.postValue(null);
            }
        });
    }
}
