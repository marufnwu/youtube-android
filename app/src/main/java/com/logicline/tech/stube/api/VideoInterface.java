package com.logicline.tech.stube.api;

import com.logicline.tech.stube.models.HomeVideo;
import com.logicline.tech.stube.models.RelatedVideo;

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

    @GET("search")
    Call<RelatedVideo> getRelatedVideo(@Query("part") String part,
                                       @Query("maxResults") int maxResult,
                                       @Query("type") String type,
                                       @Query("relatedToVideoId") String relatedVideoId,
                                       @Query("key") String key);

    @GET("search")
    Call<RelatedVideo> getRelatedVideoNextPage(@Query("part") String part,
                                               @Query("maxResults") int maxResult,
                                               @Query("type") String type,
                                               @Query("relatedToVideoId") String relatedVideoId,
                                               @Query("key") String key,
                                               @Query("pageToken") String pageToken);
}
