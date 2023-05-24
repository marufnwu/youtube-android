package com.logicline.tech.stube.api;

import androidx.lifecycle.MutableLiveData;

import com.logicline.tech.stube.models.HomeVideo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VideoInterface {

    @GET("videos")
    Call<HomeVideo> getHomeVideo(@Query("part") String part,
                                 @Query("chart") String chart,
                                 @Query("regionCode") String regionCode,
                                 @Query("maxResults") int maxResult,
                                 @Query("key") String key);


    @GET("videos")
    Call<HomeVideo> getHomeVideoNextPage(@Query("part") String part,
                                                          @Query("chart") String chart,
                                                          @Query("regionCode") String regionCode,
                                                          @Query("maxResults") int maxResult,
                                                          @Query("key") String key,
                                                          @Query("pageToken") String pageToken);
}
