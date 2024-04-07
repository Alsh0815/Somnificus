package com.x_viria.app.vita.somnificus.core;

import android.icu.util.Calendar;

public class AlarmInfo {

    public static final int WEEK__SUN = 0b1000000;
    public static final int WEEK__MON = 0b0100000;
    public static final int WEEK__TUE = 0b0010000;
    public static final int WEEK__WED = 0b0001000;
    public static final int WEEK__THU = 0b0000100;
    public static final int WEEK__FRI = 0b0000010;
    public static final int WEEK__SAT = 0b0000001;

    private AlarmTime TIME;
    private int WEEK;

    public AlarmInfo(
            AlarmTime time,
            int week
    ) {
        this.TIME = time;
        this.WEEK = week;
    }

    public Calendar getCalendar() {
        return Calendar.getInstance();
    }

}