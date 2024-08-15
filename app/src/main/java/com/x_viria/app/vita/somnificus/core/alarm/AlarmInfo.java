package com.x_viria.app.vita.somnificus.core.alarm;

import android.content.Context;
import android.icu.util.Calendar;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.R;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

public class AlarmInfo {

    public static final int WEEK__SUN = 0b11000000;
    public static final int WEEK__MON = 0b10100000;
    public static final int WEEK__TUE = 0b10010000;
    public static final int WEEK__WED = 0b10001000;
    public static final int WEEK__THU = 0b10000100;
    public static final int WEEK__FRI = 0b10000010;
    public static final int WEEK__SAT = 0b10000001;
    public static final int WEEK__ALL = WEEK__SUN | WEEK__MON | WEEK__TUE | WEEK__WED | WEEK__THU | WEEK__FRI | WEEK__SAT;

    public static final String OPT__GRA_INCREASE_VOL = "option__gradually_increase_volume";
    public static final String OPT__MUTE_VOL = "option__mute_volume";

    private final boolean ENABLE;
    private AlarmTime TIME;
    private final int WEEK;

    private String LABEL = "";

    private final Map<String, Boolean> OPTION;

    public AlarmInfo(
            AlarmTime time,
            int week,
            boolean enable
    ) {
        this.ENABLE = enable;
        this.TIME = time;
        this.WEEK = week;
        this.OPTION = new HashMap<>();
        OPTION.put(OPT__GRA_INCREASE_VOL, false);
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

    public String getLabel() {
        return LABEL;
    }

    public long getNextTime() {
        return getCalendar().getTimeInMillis();
    }

    public boolean getOption(String key) {
        return Boolean.TRUE.equals(OPTION.get(key));
    }

    public int getWeek() {
        return WEEK;
    }

    public void setLabel(String label) {
        this.LABEL = label;
    }

    public void setOption(String key, boolean val) {
        OPTION.put(key, val);
    }

    public void setTime(AlarmTime time) {
        this.TIME = time;
    }

    public void showNextTime(Context context) {
        long rem = this.getNextTime() - System.currentTimeMillis();
        int sec = (int) rem / 1000;
        int day = sec / 3600 / 24;
        int hour = sec / 3600 % 24;
        int min = (sec % 3600) / 60;
        if (1 < day) {
            Toast.makeText(
                    context,
                    String.format(
                            context.getString(R.string.activity_set_alarm__toast_alarm_was_set_for_2day_more),
                            day,
                            hour,
                            min
                    ),
                    Toast.LENGTH_SHORT
            ).show();
        } else if (day == 1) {
            Toast.makeText(
                    context,
                    String.format(
                            context.getString(R.string.activity_set_alarm__toast_alarm_was_set_for_1day),
                            hour,
                            min
                    ),
                    Toast.LENGTH_SHORT
            ).show();
        } else if (day == 0 && 0 < hour) {
            Toast.makeText(
                    context,
                    String.format(
                            context.getString(R.string.activity_set_alarm__toast_alarm_was_set_for_today_h),
                            hour,
                            min
                    ),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    context,
                    String.format(
                            context.getString(R.string.activity_set_alarm__toast_alarm_was_set_for_today_m),
                            min
                    ),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

}