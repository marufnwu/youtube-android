package com.logicline.tech.stube.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class PlayListVideo {
    public String kind;
    public String etag;
    public ArrayList<Item> items;
    public PageInfo pageInfo;
    public String nextPageToken;

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

    public class ResourceId {
        public String kind;
        public String videoId;
    }

    public class Snippet {
        public Date publishedAt;
        public String channelId;
        public String title;
        public String description;
        @Nullable
        public Thumbnails thumbnails;
        public String channelTitle;
        public String playlistId;
        public int position;
        public ResourceId resourceId;
        public String videoOwnerChannelTitle;
        public String videoOwnerChannelId;
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
