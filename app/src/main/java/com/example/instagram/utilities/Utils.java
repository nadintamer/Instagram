package com.example.instagram.utilities;

import android.util.Log;

import java.util.Date;

public class Utils {
    public static String calculateTimeAgo(Date createdAt, Boolean shortened) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + (shortened ?  "m" : " minutes ago");
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + (shortened ?  "h" : " hours ago");
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + (shortened ?  "d" : " days ago");
            }
        } catch (Exception e) {
            Log.e("Error:", "calculateTimeAgo failed", e);
        }

        return "";
    }
}
