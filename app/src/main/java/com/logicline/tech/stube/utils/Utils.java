package com.logicline.tech.stube.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Utils {


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
}

