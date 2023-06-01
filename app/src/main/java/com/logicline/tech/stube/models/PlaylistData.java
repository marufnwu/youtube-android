package com.logicline.tech.stube.models;

public class PlaylistData {
    private String playlistId;
    private String playListTitle;
    private String channelTitle;
    private String description;
    private String playListThumbnail;

    public PlaylistData(String playlistId, String playListName, String channelName, String description, String thumbnail) {
        this.playlistId = playlistId;
        this.playListTitle = playListName;
        this.channelTitle = channelName;
        this.description = description;
        this.playListThumbnail = thumbnail;
    }

    public String getPlayListTitle() {
        return playListTitle;
    }

    public void setPlayListTitle(String playListTitle) {
        this.playListTitle = playListTitle;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlayListThumbnail() {
        return playListThumbnail;
    }

    public void setPlayListThumbnail(String playListThumbnail) {
        this.playListThumbnail = playListThumbnail;
    }
}
