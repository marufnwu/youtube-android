package com.logicline.tech.stube.models;

public class ChannelData {
    private String channelId;
    private String title;
    private String description;
    private String thumbnail;

    public ChannelData(String channelId, String title, String description, String thumbnail) {
        this.channelId = channelId;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
