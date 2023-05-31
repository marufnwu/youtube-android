package com.logicline.tech.stube.models;

public class PlayerData {
    private String title;
    private String description;
    private String videoId;
    private String channelName;

    public PlayerData(String title, String description, String videoId, String channelName) {
        this.title = title;
        this.description = description;
        this.videoId = videoId;
        this.channelName = channelName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
