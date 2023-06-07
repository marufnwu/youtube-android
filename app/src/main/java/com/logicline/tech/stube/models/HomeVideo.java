package com.logicline.tech.stube.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class HomeVideo {

    public String kind;
    public String etag;
    public ArrayList<Item> items;
    public String nextPageToken;
    public String prevPageToken;
    public PageInfo pageInfo;
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

    public static class Item implements Serializable {
        public String kind;
        public String etag;
        public String id;
        public Snippet snippet;
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

    public class Localized {
        public String title;
        public String description;
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
        public ArrayList<String> tags;
        public String categoryId;
        public String liveBroadcastContent;
        public Localized localized;
        public String defaultAudioLanguage;
        public String defaultLanguage;
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
