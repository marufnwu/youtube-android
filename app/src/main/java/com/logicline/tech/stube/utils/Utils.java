package com.logicline.tech.stube.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    private static final String[] months = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    /**
     * Find system region code
     *
     * @param context context of the application
     * @return region code ex- bd, usa
     */
    public static String getRegionCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimCountryIso();
    }

    /**
     * show large size log message
     * 4000 char size is default log message size it would split message into parts
     * and then show that message
     *
     * @param tag     that log tag
     * @param message message
     */
    public static void showLongLogMsg(String tag, String message) {
        if (message.length() > 4000) {
            List<String> splits = new ArrayList<>();
            int index = 0;
            while (index < message.length()) {
                splits.add(message.substring(index, Math.min(index + 4000, message.length())));
                index += 4000;
            }

            for (String s : splits) {
                Log.d(tag, "LogMsg: " + s);
            }
        } else {
            Log.d(tag, "LogMsg: " + message);
        }
    }

    public static String getDateString(Date date) {
        String sb = months[date.getMonth()] +
                " " + date.getDate();

        return sb;
    }

    /**
     *
     * @param number that need to short
     * @return the shorten number as String.
     */
    public static String shortenNumber(double number) {
        String result;
        if (number >= 1e9) {
            // Convert to billions
            result = String.format("%.1fB", number / 1e9);
        } else if (number >= 1e6) {
            // Convert to millions
            result = String.format("%.1fM", number / 1e6);
        } else if (number >= 1e3) {
            // Convert to thousands
            result = String.format("%.1fK", number / 1e3);
        } else {
            // Keep the number as is
            result = String.format("%.0f", number);
        }
        return result;
    }
}

