package com.nectar.timeby.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by finalize on 7/27/15.
 */
public class TimeUtil {
    private static final String TAG = "TimeUtil";

    public static String getTimeStr(long time) {
        DateFormat formator = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formator.format(new Date(time));
    }

    public static long getTime(String timeStr) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = 0;
        try {
            time = format.parse(timeStr).getTime();
        } catch (ParseException e) {
            Log.w(TAG, e.getMessage());
        }
        return time;
    }
}
