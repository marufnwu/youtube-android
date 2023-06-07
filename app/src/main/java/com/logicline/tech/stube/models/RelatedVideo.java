package com.logicline.tech.stube.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class RelatedVideo {
    public String kind;
    public String etag;
    public String nextPageToken;
    public String prevPageToken;
    public PageInfo pageInfo;
    public ArrayList<Item> items;
    public Error error;

    public class Error {
        public int code;
        public String message;
        public ArrayList<Error2> errors;
    }

    public class Error2 {
        public String message;
        public String domain;
        public String reason;
    }

    public class Default {
        public String url;
        public int width;
        public int height;
    }

    public class High {
        public String url;
        public int width;
        public int height;
    }

    public class Id {
        public String kind;
        public String videoId;
    }

    public class Item {
        public String kind;
        public String etag;
        public Id id;
        public Snippet snippet;
    }

    public class Maxres {
        public String url;
        public int width;
        public int height;
    }

    public class Medium {
        public String url;
        public int width;
        public int height;
    }

    public class PageInfo {
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

    public class Standard {
        public String url;
        public int width;
        public int height;
    }

    public class Thumbnails {
        @SerializedName("default")
        public Default mydefault;
        public Medium medium;
        public High high;
        public Standard standard;
        public Maxres maxres;
    }
}
