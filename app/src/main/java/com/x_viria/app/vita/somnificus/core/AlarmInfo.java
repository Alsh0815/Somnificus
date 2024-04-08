package com.x_viria.app.vita.somnificus.core;

import android.icu.util.Calendar;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class AlarmInfo {

    public static final int WEEK__SUN = 0b11000000;
    public static final int WEEK__MON = 0b10100000;
    public static final int WEEK__TUE = 0b10010000;
    public static final int WEEK__WED = 0b10001000;
    public static final int WEEK__THU = 0b10000100;
    public static final int WEEK__FRI = 0b10000010;
    public static final int WEEK__SAT = 0b10000001;
    public static final int WEEK__ALL = WEEK__SUN | WEEK__MON | WEEK__TUE | WEEK__WED | WEEK__THU | WEEK__FRI | WEEK__SAT;

    private final boolean ENABLE;
    private final AlarmTime TIME;
    private final int WEEK;

    public AlarmInfo(
            AlarmTime time,
            int week,
            boolean enable
    ) {
        this.ENABLE = enable;
        this.TIME = time;
        this.WEEK = week;
    }

    public Calendar getCalendar() {
        LocalDateTime nowTime = LocalDateTime.now();

        LocalDateTime nearest = LocalDateTime.now().plusDays(14);
        int[] wIDX = new int[] {WEEK__SUN, WEEK__MON, WEEK__TUE, WEEK__WED, WEEK__THU, WEEK__FRI, WEEK__SAT};
        DayOfWeek[] wIDXD = new DayOfWeek[] {DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY};
        for (int i = 0; i < wIDX.length; i++) {
            if ((WEEK & wIDX[i]) != wIDX[i]) continue;
            LocalDateTime d = nowTime.with(TemporalAdjusters.nextOrSame(wIDXD[i]));
            d = LocalDateTime.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth(), TIME.getH(), TIME.getM(), TIME.getS());
            if (d.isAfter(nowTime) && d.isBefore(nearest)) {
                nearest = d;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(nearest.getYear(), nearest.getMonthValue() - 1, nearest.getDayOfMonth(), TIME.getH(), TIME.getM(), TIME.getS());

        return calendar;
    }

    public AlarmTime getAlarmTime() {
        return TIME;
    }

    public boolean getEnable() {
        return ENABLE;
    }

    public int getWeek() {
        return WEEK;
    }

}