package com.logicline.tech.stube.models;

import java.util.ArrayList;
import java.util.Date;

public class CommentThread {
    public String kind;
    public String etag;
    public String nextPageToken;
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

    public class AuthorChannelId {
        public String value;
    }

    public class Item {
        public String kind;
        public String etag;
        public String id;
        public Snippet snippet;
    }

    public class PageInfo {
        public int totalResults;
        public int resultsPerPage;
    }

    public class Snippet {
        public String videoId;
        public TopLevelComment topLevelComment;
        public boolean canReply;
        public int totalReplyCount;
        public boolean isPublic;
        public String textDisplay;
        public String textOriginal;
        public String authorDisplayName;
        public String authorProfileImageUrl;
        public String authorChannelUrl;
        public AuthorChannelId authorChannelId;
        public boolean canRate;
        public String viewerRating;
        public int likeCount;
        public Date publishedAt;
        public Date updatedAt;
    }

    public class TopLevelComment {
        public String kind;
        public String etag;
        public String id;
        public Snippet snippet;
    }
}
