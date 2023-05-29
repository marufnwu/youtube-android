package com.logicline.tech.stube.ui.activities.channelActivity;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.logicline.tech.stube.api.ApiClient;
import com.logicline.tech.stube.api.VideoInterface;
import com.logicline.tech.stube.constants.ApiConstants;
import com.logicline.tech.stube.models.ChannelVideo;
import com.logicline.tech.stube.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelViewModel extends AndroidViewModel {
    private static final String TAG = "ChannelViewModel";
    private final MutableLiveData<ChannelVideo> channelVideo;
    private final MutableLiveData<ChannelVideo> nextPage;
    private final VideoInterface videoInterface;
    private String nextPageToken;

    public ChannelViewModel(@NonNull Application application) {
        super(application);

        channelVideo = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();
        videoInterface = ApiClient.getInstance(
                        ApiConstants.API_BASE_URL)
                .create(VideoInterface.class);
    }

    public MutableLiveData<ChannelVideo> getChannelVideoItems(String channelId) {
        Call<ChannelVideo> video = videoInterface.getChannelVideos(channelId,
                ApiConstants.API_ORDER_DATE, ApiConstants.API_KEY);
        if (video == null)
            return null;

        video.enqueue(new Callback<ChannelVideo>() {
            @Override
            public void onResponse(Call<ChannelVideo> call, Response<ChannelVideo> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        channelVideo.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else {
                        Utils.showLongLogMsg("response", response.toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<ChannelVideo> call, Throwable t) {
                channelVideo.postValue(null);
            }
        });

        return channelVideo;
    }

    public MutableLiveData<ChannelVideo> getNextPage(String channelId) {
        if (nextPageToken == null) {
            Log.d(TAG, "getNextPage: no next page found");
            return null;
        }

        Call<ChannelVideo> response = videoInterface.getChannelVideosNextPage(channelId,
                ApiConstants.API_ORDER_DATE, ApiConstants.API_KEY, nextPageToken);
        response.enqueue(new Callback<ChannelVideo>() {
            @Override
            public void onResponse(Call<ChannelVideo> call, Response<ChannelVideo> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        nextPage.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else {
                        Utils.showLongLogMsg("response", response.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ChannelVideo> call, Throwable t) {
                nextPage.postValue(null);
            }
        });
        return nextPage;
    }
}
