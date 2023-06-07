package com.logicline.tech.stube.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class ChannelDetails {
    public String kind;
    public String etag;
    public PageInfo pageInfo;
    public ArrayList<Item> items;
    public SearchItem.Error error;

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

    public class Item {
        public String kind;
        public String etag;
        public String id;
        public Snippet snippet;
        public Statistics statistics;
    }

    public class Localized {
        public String title;
        public String description;
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
        public String title;
        public String description;
        public String customUrl;
        public Date publishedAt;
        public Thumbnails thumbnails;
        public Localized localized;
        public String country;
    }

    public class Statistics {
        public String viewCount;
        public String subscriberCount;
        public boolean hiddenSubscriberCount;
        public String videoCount;
    }

    public class Thumbnails {
        @SerializedName("default")
        public Default mydefault;
        public Medium medium;
        public High high;
    }
}
