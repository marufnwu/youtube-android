package com.logicline.tech.stube.api;

import com.logicline.tech.stube.models.ChannelDetails;
import com.logicline.tech.stube.models.ChannelVideo;
import com.logicline.tech.stube.models.CommentThread;
import com.logicline.tech.stube.models.HomeVideo;
import com.logicline.tech.stube.models.PlayListVideo;
import com.logicline.tech.stube.models.RelatedVideo;
import com.logicline.tech.stube.models.SearchItem;
import com.logicline.tech.stube.models.VideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VideoInterface {

    @GET("videos")
    Call<HomeVideo> getHomeVideo(@Query("part") String part,
                                 @Query("chart") String chart,
                                 @Query("regionCode") String regionCode,
                                 @Query("key") String key);

    @GET("videos")
    Call<HomeVideo> getHomeVideoNextPage(@Query("part") String part,
                                         @Query("chart") String chart,
                                         @Query("regionCode") String regionCode,
                                         @Query("key") String key,
                                         @Query("pageToken") String pageToken);

    @GET("search")
    Call<RelatedVideo> getRelatedVideo(@Query("part") String part,
                                       @Query("type") String type,
                                       @Query("relatedToVideoId") String relatedVideoId,
                                       @Query("key") String key);

    @GET("search")
    Call<RelatedVideo> getRelatedVideoNextPage(@Query("part") String part,
                                               @Query("type") String type,
                                               @Query("relatedToVideoId") String relatedVideoId,
                                               @Query("key") String key,
                                               @Query("pageToken") String pageToken);

    @GET("search")
    Call<SearchItem> getSearchResult(@Query("part") String part,
                                     @Query("q") String query,
                                     @Query("key") String key,
                                     @Query("type") String type);
    @GET("search")
    Call<SearchItem> getSearchResult(@Query("part") String part,
                                     @Query("q") String query,
                                     @Query("key") String key);

    @GET("search")
    Call<SearchItem> getSearchResultNextPage(@Query("part") String part,
                                             @Query("q") String query,
                                             @Query("key") String key,
                                             @Query("pageToken") String pageToken);

    @GET("search")
    Call<ChannelVideo> getChannelVideos(@Query("part") String part,
                                        @Query("channelId") String channelId,
                                        @Query("order") String order,
                                        @Query("type") String type,
                                        @Query("key") String key);

    @GET("search")
    Call<ChannelVideo> getChannelVideosNextPage(@Query("part") String part,
                                                @Query("channelId") String channelId,
                                                @Query("order") String order,
                                                @Query("type") String type,
                                                @Query("key") String key,
                                                @Query("pageToken") String pageToken);

    @GET("channels")
    Call<ChannelDetails> getChannelDetails(@Query("part") String part,
                                           @Query("part") String part1,
                                           @Query("id") String id,
                                           @Query("key") String key);

    @GET("videos")
    Call<VideoDetails>getVideoDetails(@Query("part") String part,
                                      @Query("part") String part1,
                                      @Query("id") String id,
                                      @Query("key") String key);

    @GET("commentThreads")
    Call<CommentThread> getCommentThread(@Query("part") String part,
                                         @Query("videoId") String videoId,
                                         @Query("key") String key);

    @GET("commentThreads")
    Call<CommentThread> getCommentThreadNextPage(@Query("part") String part,
                                         @Query("videoId") String videoId,
                                         @Query("key") String key,
                                         @Query("pageToken") String pageToken);

    @GET("playlistItems")
    Call<PlayListVideo> getPlayListVideo(@Query("part") String part,
                                         @Query("playlistId") String playListId,
                                         @Query("key") String key);
    Call<PlayListVideo> getPlayListVideoNextPage(@Query("part") String part,
                                                 @Query("playlistId") String playListId,
                                                 @Query("key") String key,
                                                 @Query("pageToken") String pageToken);
}
