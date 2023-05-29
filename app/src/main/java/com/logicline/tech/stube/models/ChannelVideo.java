package com.logicline.tech.stube.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class ChannelVideo {
    public String kind;
    public String etag;
    public String nextPageToken;
    public String regionCode;
    public PageInfo pageInfo;
    public ArrayList<Item> items;

    public static class Default {
        public String url;
        public int width;
        public int height;
    }

    public static class High {
        public String url;
        public int width;
        public int height;
    }

    public static class Id {
        public String kind;
        public String videoId;
    }

    public class Item {
        public String kind;
        public String etag;
        public Id id;
        public Snippet snippet;
    }

    public static class Medium {
        public String url;
        public int width;
        public int height;
    }

    public static class PageInfo {
        public int totalResults;
        public int resultsPerPage;
    }

    public class Snippet {
        public Date publishedAt;
        public String channelId;
        public String title;
        public String description;
        public Thumbnails thumbnails;
        public String channelTitle;
        public String liveBroadcastContent;
        public Date publishTime;
    }

    public static class Thumbnails {
        @SerializedName("default")
        public Default mydefault;
        public Medium medium;
        public High high;
    }
}
