package com.logicline.tech.stube.models;

public class PlayerPlayListItem {
    private String videoTitle;
    private String videoId;
    private String videoImageUrl;
    private String channelName;
    private boolean isPlaying;
    private int position;

    public PlayerPlayListItem(String videoTitle, String videoId, String videoImageUrl,
                              String channelName, boolean isPlaying) {
        this.videoTitle = videoTitle;
        this.videoId = videoId;
        this.videoImageUrl = videoImageUrl;
        this.channelName = channelName;
        this.isPlaying = isPlaying;
        this.position = -1;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoImageUrl() {
        return videoImageUrl;
    }

    public void setVideoImageUrl(String videoImageUrl) {
        this.videoImageUrl = videoImageUrl;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}