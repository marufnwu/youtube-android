package com.logicline.tech.stube.ui.activities.MainActivity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.logicline.tech.stube.api.ApiClient;
import com.logicline.tech.stube.api.VideoInterface;
import com.logicline.tech.stube.constants.ApiConstants;
import com.logicline.tech.stube.models.HomeVideo;
import com.logicline.tech.stube.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private final String regionCode;
    private final Context context;
    private final MutableLiveData<HomeVideo> homeVideo;
    private final MutableLiveData<HomeVideo> nextPage;
    private final VideoInterface videoInterface;
    private String nextPageToken;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        homeVideo = new MutableLiveData<>();
        nextPage = new MutableLiveData<>();

        videoInterface = ApiClient.getInstance(
                        ApiConstants.API_BASE_URL)
                .create(VideoInterface.class);

        // Get region code
        regionCode = Utils.getRegionCode(context);

        //calling api
        Call<HomeVideo> video = videoInterface.getHomeVideo(
                ApiConstants.API_PART_SNIPPET, ApiConstants.API_CHART_MOST_POPULAR,
                regionCode, ApiConstants.API_KEY);

        video.enqueue(new Callback<HomeVideo>() {
            @Override
            public void onResponse(Call<HomeVideo> call,
                                   Response<HomeVideo> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        homeVideo.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else
                        Utils.showLongLogMsg("response", response.toString());
                }else {
                    Log.d(TAG, "onResponse: error " + response);
                }
            }

            @Override
            public void onFailure(Call<HomeVideo> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                homeVideo.postValue(null);
            }
        });
    }

    public MutableLiveData<HomeVideo> getHomeVideos() {
        return homeVideo;
    }

    public MutableLiveData<HomeVideo> getNextPage() {
        return nextPage;
    }

    public void getHomeVideoNextPage() {
        if (nextPageToken == null) {
            Log.d(TAG, "getNextPage: no next page found");
            return;
        }

        Call<HomeVideo> video = videoInterface.getHomeVideoNextPage(
                ApiConstants.API_PART_SNIPPET, ApiConstants.API_CHART_MOST_POPULAR, regionCode,
                ApiConstants.API_KEY, nextPageToken);


        video.enqueue(new Callback<HomeVideo>() {
            @Override
            public void onResponse(Call<HomeVideo> call, Response<HomeVideo> response) {
                if (response.isSuccessful()) {
                    //homevideo = response.body();
                    if (response.body() != null) {
                        nextPage.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else
                        Utils.showLongLogMsg("response", response.toString());
                }
            }

            @Override
            public void onFailure(Call<HomeVideo> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                nextPage.postValue(null);
            }
        });
    }


}
