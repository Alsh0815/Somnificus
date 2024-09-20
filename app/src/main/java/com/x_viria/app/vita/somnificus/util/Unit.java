package com.x_viria.app.vita.somnificus.util;

import android.content.Context;

import com.x_viria.app.vita.somnificus.util.format.TimeFormat;

public class Unit {

    public static class Pixel {

        public static int dp2px(Context context, float dp) {
            return (int) (dp * context.getResources().getDisplayMetrics().density);
        }

    }

    public static class Time {

        public static TimeFormat toTime(long millisecond) {
            int hours = (int) (millisecond / (60 * 60 * 1000));
            millisecond %= 60 * 60 * 1000;
            int minutes = (int) (millisecond / (60 * 1000));
            millisecond %= 60 * 1000;
            int seconds = (int) (millisecond / 1000);
            millisecond %= 1000;
            TimeFormat format = new TimeFormat();
            format.HOUR = hours;
            format.MINUTE = minutes;
            format.SECOND = seconds;
            format.MILLISECOND = (int) millisecond;
            return format;
        }

        public static int toMinutes(long millisecond) {
            return (int) (millisecond / (60 * 1000));
        }

    }

}
