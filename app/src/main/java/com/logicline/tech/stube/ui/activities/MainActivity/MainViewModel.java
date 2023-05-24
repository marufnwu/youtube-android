package com.logicline.tech.stube.ui.activities.MainActivity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.logicline.tech.stube.api.ApiClient;
import com.logicline.tech.stube.api.VideoInterface;
import com.logicline.tech.stube.models.HomeVideo;
import com.logicline.tech.stube.constants.Constants;
import com.logicline.tech.stube.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private final String regionCode;
    private final Context context;
    private MutableLiveData<HomeVideo> homeVideo;
    private final VideoInterface videoInterface;
    private String nextPageToken;
    private boolean hasNextPage = false;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        homeVideo = new MutableLiveData<>();
        videoInterface = ApiClient.getInstance(
                        Constants.HOME_VIDEO_URL_BASE_URL)
                .create(VideoInterface.class);

        // Get region code
        regionCode = Utils.getRegionCode(context);

        //calling api
        Call<HomeVideo> video = videoInterface.getHomeVideo(Constants.HOME_VIDEO_API_PART,
                Constants.HOME_VIDEO_API_CHART, regionCode, Constants.HOME_VIDEO_API_MAX_RESULT,
                Constants.HOME_VIDEO_API_KEY);

        video.enqueue(new Callback<HomeVideo>() {
            @Override
            public void onResponse(Call<HomeVideo> call,
                                   Response<HomeVideo> response) {
                if (response.isSuccessful()) {
                    //homevideo = response.body();
                    if (response.body()!= null){
                        homeVideo.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    }
                    else
                        Utils.showLongLogMsg("response", response.toString());
                }
            }

            @Override
            public void onFailure(Call<HomeVideo> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                homeVideo.postValue(null);
            }
        });
    }

    public MutableLiveData<HomeVideo> getHomeVideos(){
        return homeVideo;
    }

    public MutableLiveData<HomeVideo> getHomeVideoNextPage(){
        if (nextPageToken == null){
            Log.d(TAG, "getNextPage: no next page found");
            return null;
        }

        Call<HomeVideo> video = videoInterface.getHomeVideoNextPage(Constants.HOME_VIDEO_API_PART,
                Constants.HOME_VIDEO_API_CHART, regionCode, Constants.HOME_VIDEO_API_MAX_RESULT,
                Constants.HOME_VIDEO_API_KEY, nextPageToken);
        MutableLiveData<HomeVideo> nextVideo = new MutableLiveData<>();

        video.enqueue(new Callback<HomeVideo>() {
            @Override
            public void onResponse(Call<HomeVideo> call, Response<HomeVideo> response) {
                if (response.isSuccessful()) {
                    //homevideo = response.body();
                    if (response.body()!= null){
                        nextVideo.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    }

                    else
                        Utils.showLongLogMsg("response", response.toString());
                }
            }

            @Override
            public void onFailure(Call<HomeVideo> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                nextVideo.postValue(null);
            }
        });

        return nextVideo;
    }
}
