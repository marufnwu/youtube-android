package com.logicline.tech.stube.api;

import com.logicline.tech.stube.models.ChannelVideo;
import com.logicline.tech.stube.models.HomeVideo;
import com.logicline.tech.stube.models.RelatedVideo;
import com.logicline.tech.stube.models.SearchItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VideoInterface {

    @GET("videos")
    Call<HomeVideo> getHomeVideo(@Query("chart") String chart,
                                 @Query("regionCode") String regionCode,
                                 @Query("key") String key);


    @GET("videos")
    Call<HomeVideo> getHomeVideoNextPage(@Query("chart") String chart,
                                         @Query("regionCode") String regionCode,
                                         @Query("key") String key,
                                         @Query("pageToken") String pageToken);

    @GET("search")
    Call<RelatedVideo> getRelatedVideo(@Query("type") String type,
                                       @Query("relatedToVideoId") String relatedVideoId,
                                       @Query("key") String key);

    @GET("search")
    Call<RelatedVideo> getRelatedVideoNextPage(@Query("type") String type,
                                               @Query("relatedToVideoId") String relatedVideoId,
                                               @Query("key") String key,
                                               @Query("pageToken") String pageToken);

    @GET("search")
    Call<SearchItem> getSearchResult(@Query("q") String query,
                                     @Query("key") String key);

    @GET("search")
    Call<ChannelVideo> getChannelVideos(@Query("channelId") String channelId,
                                        @Query("order") String order,
                                        @Query("key") String key);
    @GET("search")
    Call<ChannelVideo> getChannelVideosNextPage(@Query("channelId") String channelId,
                                                @Query("order") String order,
                                                @Query("key") String key,
                                                @Query("pageToken") String pageToken);
}
