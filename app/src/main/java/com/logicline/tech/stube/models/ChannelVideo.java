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
    public SearchItem.Error error;

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

    public static class Medium {
        public String url;
        public int width;
        public int height;
    }

    public static class PageInfo {
        public int totalResults;
        public int resultsPerPage;
    }

    public static class Thumbnails {
        @SerializedName("default")
        public Default mydefault;
        public Medium medium;
        public High high;
    }

    public class Error {
        public int code;
        public String message;
        public ArrayList<SearchItem.Error2> errors;
    }

    public class Error2 {
        public String message;
        public String domain;
        public String reason;
    }

    public class Item {
        public String kind;
        public String etag;
        public Id id;
        public Snippet snippet;
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
}
