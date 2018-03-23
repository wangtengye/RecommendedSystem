package com.se.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by takahiro on 2017/7/2.
 */
public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String longToDate(long time) {
        return sdf.format(new Date(time));
    }
    public static String longToTime(long time) {
        time /= 1000;
        long h = time/3600;
        time %= 3600;
        long m = time/60;
        time %= 60;
        long s = time;
        String ret = String.format("%02d:%02d:%02d", h, m, s);
        return ret;
    }
}
