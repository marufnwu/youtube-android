package com.logicline.tech.stube.utils;

public class NumberUtils {
    public static int normalize(int value, int oldMin, int oldMax, int newMin, int newMax) {
        int oldRange = oldMax - oldMin;
        int newRange = newMax - newMin;

        return ((value - oldMin) * newRange / oldRange) + newMin;
    }

    public static float normalize(float value, float oldMin, float oldMax, float newMin, float newMax) {
        float oldRange = oldMax - oldMin;
        float newRange = newMax - newMin;

        return ((value - oldMin) * newRange / oldRange) + newMin;
    }

    public static long normalize(long value, long oldMin, long oldMax, long newMin, long newMax) {
        long oldRange = oldMax - oldMin;
        long newRange = newMax - newMin;

        return ((value - oldMin) * newRange / oldRange) + newMin;
    }
}

