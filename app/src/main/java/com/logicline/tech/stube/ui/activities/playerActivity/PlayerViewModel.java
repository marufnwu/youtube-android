package com.logicline.tech.stube.ui.activities.playerActivity;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.logicline.tech.stube.api.ApiClient;
import com.logicline.tech.stube.api.VideoInterface;
import com.logicline.tech.stube.constants.ApiConstants;
import com.logicline.tech.stube.models.CommentThread;
import com.logicline.tech.stube.models.RelatedVideo;
import com.logicline.tech.stube.models.VideoDetails;
import com.logicline.tech.stube.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerViewModel extends AndroidViewModel {
    private static final String TAG = "PlayerViewModel";
    private final MutableLiveData<RelatedVideo> apiResponse;
    private final MutableLiveData<VideoDetails> videoDetails;
    private final MutableLiveData<CommentThread> commentThreadMutableLiveData;
    private final MutableLiveData<CommentThread> commentThreadNextPageLiveData;
    private final VideoInterface videoInterface;
    public MutableLiveData<Boolean> isMiniPlayerVisible = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFullscreen = new MutableLiveData<>();
    private String nextPageToken;
    private String commentThreadNextPageToken;

    public PlayerViewModel(@NonNull Application application) {
        super(application);

        apiResponse = new MutableLiveData<>();
        videoDetails = new MutableLiveData<>();
        commentThreadMutableLiveData = new MutableLiveData<>();
        commentThreadNextPageLiveData = new MutableLiveData<>();

        isMiniPlayerVisible.setValue(false);
        isFullscreen.setValue(false);

        videoInterface = ApiClient.getInstance(ApiConstants.API_BASE_URL)
                .create(VideoInterface.class);

    }

    public MutableLiveData<RelatedVideo> getRelatedVideo() {
        return apiResponse;
    }

    public MutableLiveData<VideoDetails> getVideoDetails() {
        return videoDetails;
    }

    public MutableLiveData<CommentThread> getCommentThread() {
        return commentThreadMutableLiveData;
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

    public MutableLiveData<CommentThread> getCommentThreadNextPage() {
        return commentThreadNextPageLiveData;
    }

    public void loadRelatedVideos(String relatedVideoId) {
        Log.d(TAG, "getRelatedVideos: is called");
        Call<RelatedVideo> video = videoInterface.getRelatedVideo(
                ApiConstants.API_PART_SNIPPET, ApiConstants.API_TYPE_VIDEO,
                relatedVideoId, ApiConstants.API_KEY);

        video.enqueue(new Callback<RelatedVideo>() {
            @Override
            public void onResponse(Call<RelatedVideo> call, Response<RelatedVideo> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        apiResponse.postValue(response.body());
                        nextPageToken = response.body().nextPageToken;
                    } else
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

    public void loadVideoDetails(String videoId) {
        Call<VideoDetails> videoDetailsCall = videoInterface.getVideoDetails(ApiConstants.API_PART_STATISTICS,
                ApiConstants.API_PART_SNIPPET,
                videoId, ApiConstants.API_KEY);

        videoDetailsCall.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse(Call<VideoDetails> call, Response<VideoDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        videoDetails.postValue(response.body());
                    } else
                        Utils.showLongLogMsg("response video details ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<VideoDetails> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                Log.d(TAG, "onFailure: " + t);
                apiResponse.postValue(null);
            }
        });
    }

    public void loadCommentThread(String videoId) {
        Call<CommentThread> commentThreadCall =
                videoInterface.getCommentThread(ApiConstants.API_PART_SNIPPET,
                        videoId, ApiConstants.API_KEY);
        commentThreadCall.enqueue(new Callback<CommentThread>() {
            @Override
            public void onResponse(Call<CommentThread> call, Response<CommentThread> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        commentThreadMutableLiveData.postValue(response.body());
                        commentThreadNextPageToken = response.body().nextPageToken;
                    } else
                        Utils.showLongLogMsg("response video commentThread ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<CommentThread> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                Log.d(TAG, "onFailure: " + t);
                commentThreadMutableLiveData.postValue(null);
            }
        });
    }

    public void loadCommentThreadNextPage(String videoId) {
        Call<CommentThread> commentThreadCall =
                videoInterface.getCommentThreadNextPage(ApiConstants.API_PART_SNIPPET,
                        videoId, ApiConstants.API_KEY, commentThreadNextPageToken);
        commentThreadCall.enqueue(new Callback<CommentThread>() {
            @Override
            public void onResponse(Call<CommentThread> call, Response<CommentThread> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                        commentThreadNextPageLiveData.postValue(response.body());
                        commentThreadNextPageToken = response.body().nextPageToken;
                        Log.d(TAG, "onResponse: next page token " + commentThreadNextPageToken);
                    } else
                        Utils.showLongLogMsg("response video commentThread ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<CommentThread> call, Throwable t) {
                Log.d(TAG, "onFailure: error in response");
                Log.d(TAG, "onFailure: " + t);
                commentThreadMutableLiveData.postValue(null);
            }
        });
    }

    public MutableLiveData<Boolean> isMiniPlayerVisible() {
        return isMiniPlayerVisible;
    }

    public MutableLiveData<Boolean> isFullscreen() {
        return isFullscreen;
    }
}
