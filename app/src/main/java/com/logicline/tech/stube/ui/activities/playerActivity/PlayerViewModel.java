package com.logicline.tech.stube.ui.activities.playerActivity;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.logicline.tech.stube.api.ApiClient;
import com.logicline.tech.stube.api.VideoInterface;
import com.logicline.tech.stube.constants.ApiConstants;
import com.logicline.tech.stube.models.RelatedVideo;
import com.logicline.tech.stube.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerViewModel extends AndroidViewModel {
    private static final String TAG = "PlayerViewModel";
    private final MutableLiveData<RelatedVideo> apiResponse;
    private final VideoInterface videoInterface;
    private String nextPageToken;

    public PlayerViewModel(@NonNull Application application) {
        super(application);

        apiResponse = new MutableLiveData<>();
        videoInterface = ApiClient.getInstance(ApiConstants.API_BASE_URL)
                .create(VideoInterface.class);

    }

    public MutableLiveData<RelatedVideo> getRelatedVideo() {
        return apiResponse;
    }

    public void loadRelatedVideos(String relatedVideoId){
        Log.d(TAG, "getRelatedVideos: is called");
        Call<RelatedVideo> video = videoInterface.getRelatedVideo(ApiConstants.API_TYPE_VIDEO,
                relatedVideoId, ApiConstants.API_KEY);

        video.enqueue(new Callback<RelatedVideo>() {
            @Override
            public void onResponse(Call<RelatedVideo> call, Response<RelatedVideo> response) {
                if (response.isSuccessful()){
                    if (response.body()!= null){
                        apiResponse.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    }else
                        Utils.showLongLogMsg("response related video", response.toString());
                }
            }

            @Override
            public void onFailure(Call<RelatedVideo> call, Throwable t) {

                Log.d(TAG, "onFailure: error in response");
                Log.d(TAG, "onFailure: " + t);
                apiResponse.postValue(null);
            }
        });
    }

    /*
    public MutableLiveData<RelatedVideo> getRelatedVideoNextPage(String relatedVideoId){
        if (nextPageToken == null){
            Log.d(TAG, "getNextPage: no next page found");
            return null;
        }

        Call<RelatedVideo> video = videoInterface.getRelatedVideoNextPage(ApiConstants.API_PART_SNIPPET,
                ApiConstants.RELATED_VIDEO_API_MAX_RESULT, ApiConstants.API_TYPE_VIDEO,
                relatedVideoId, ApiConstants.API_KEY, nextPageToken);

        MutableLiveData<RelatedVideo> apiResponseNextPage = new MutableLiveData<>();

        video.enqueue(new Callback<RelatedVideo>() {
            @Override
            public void onResponse(Call<RelatedVideo> call, Response<RelatedVideo> response) {
                if (response.isSuccessful()){
                    if (response.body()!= null){
                        apiResponseNextPage.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    }else
                        Utils.showLongLogMsg("response related video", response.toString());
                }
            }

            @Override
            public void onFailure(Call<RelatedVideo> call, Throwable t) {

                Log.d(TAG, "onFailure: error in response");
                apiResponseNextPage.postValue(null);
            }
        });
        return apiResponseNextPage;
    }*/
}
