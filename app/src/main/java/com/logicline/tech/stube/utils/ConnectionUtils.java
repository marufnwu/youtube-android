package com.logicline.tech.stube.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionUtils {
    private static final String TAG = "ConnectionUtils";
    public static String getYoutubeVideoIdFromUrl(String url) {
        String vId = null;

        String regexPattern = "^https:\\/\\/www\\.youtube\\.com\\/watch\\?v=([\\w-]+)";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            vId = matcher.group(1);
        }
        return vId;
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
