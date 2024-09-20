package com.x_viria.app.vita.somnificus.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;

import com.x_viria.app.vita.somnificus.activity.BedTimeActivity;
import com.x_viria.app.vita.somnificus.receiver.BedTimeBroadcastReceiver;
import com.x_viria.app.vita.somnificus.util.Unit;
import com.x_viria.app.vita.somnificus.util.format.TimeFormat;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

public class BedTimeManager {

    private final Context CONTEXT;

    public BedTimeManager(Context context) {
        this.CONTEXT = context;
    }

    private void cancel(int flag_dow) {
        Intent intent = new Intent(CONTEXT, BedTimeBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CONTEXT, flag_dow + 256, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void setReminder(int flag_dow, int hour, int minute) {
        SPStorage sps = new SPStorage(CONTEXT);
        if (!sps.getBool(Config.KEY__SETTINGS_ENABLE_BED_TIME_REMIND, SPDefault.SETTINGS_ENABLE_BED_TIME_REMIND)) return;
        long remind_min = sps.getLong(Config.KEY__SETTINGS_BED_TIME_REMIND, SPDefault.SETTINGS_BED_TIME_REMIND);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        calendar.add(Calendar.MINUTE, -1 * Unit.Time.toMinutes(remind_min));

        Log.d("BedTimeManager", String.format(
                "Set reminder -> %d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        ));

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int daysUntilAlarm = (flag_dow - currentDayOfWeek + 7) % 7;
        if (daysUntilAlarm == 0 && calendar.getTimeInMillis() < System.currentTimeMillis()) {
            daysUntilAlarm = 7;
        }
        calendar.add(Calendar.DAY_OF_YEAR, daysUntilAlarm);

        Intent intent = new Intent(CONTEXT, BedTimeBroadcastReceiver.class);
        intent.putExtra("MINUTE", remind_min);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CONTEXT, flag_dow + 256, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public long getEventToday() {
        SPStorage sps = new SPStorage(CONTEXT);
        int flag_dow = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        int todayFlag = 1 << (today - 1);

        if ((flag_dow & todayFlag) != 0) {
            switch (today) {
                case Calendar.SUNDAY:
                    return sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_SUN, SPDefault.SETTINGS_BED_TIME_D_MILLI);
                case Calendar.MONDAY:
                    return sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_MON, SPDefault.SETTINGS_BED_TIME_D_MILLI);
                case Calendar.TUESDAY:
                    return sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_TUE, SPDefault.SETTINGS_BED_TIME_D_MILLI);
                case Calendar.WEDNESDAY:
                    return sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_WED, SPDefault.SETTINGS_BED_TIME_D_MILLI);
                case Calendar.THURSDAY:
                    return sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_THU, SPDefault.SETTINGS_BED_TIME_D_MILLI);
                case Calendar.FRIDAY:
                    return sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_FRI, SPDefault.SETTINGS_BED_TIME_D_MILLI);
                case Calendar.SATURDAY:
                    return sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_SAT, SPDefault.SETTINGS_BED_TIME_D_MILLI);
            }
        }
        return -1;
    }

    public void sync() {
        SPStorage sps = new SPStorage(CONTEXT);

        int flag_dow = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
        if ((flag_dow & BedTimeActivity.FLAG_SUN) == BedTimeActivity.FLAG_SUN) {
            long time = sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_SUN, SPDefault.SETTINGS_BED_TIME_D_MILLI);
            TimeFormat timeFormat = Unit.Time.toTime(time);
            setReminder(1, timeFormat.HOUR, timeFormat.MINUTE);
        } else {
            cancel(1);
        }
        if ((flag_dow & BedTimeActivity.FLAG_MON) == BedTimeActivity.FLAG_MON) {
            long time = sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_MON, SPDefault.SETTINGS_BED_TIME_D_MILLI);
            TimeFormat timeFormat = Unit.Time.toTime(time);
            setReminder(2, timeFormat.HOUR, timeFormat.MINUTE);
        } else {
            cancel(2);
        }
        if ((flag_dow & BedTimeActivity.FLAG_TUE) == BedTimeActivity.FLAG_TUE) {
            long time = sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_TUE, SPDefault.SETTINGS_BED_TIME_D_MILLI);
            TimeFormat timeFormat = Unit.Time.toTime(time);
            setReminder(3, timeFormat.HOUR, timeFormat.MINUTE);
        } else {
            cancel(3);
        }
        if ((flag_dow & BedTimeActivity.FLAG_WED) == BedTimeActivity.FLAG_WED) {
            long time = sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_WED, SPDefault.SETTINGS_BED_TIME_D_MILLI);
            TimeFormat timeFormat = Unit.Time.toTime(time);
            setReminder(4, timeFormat.HOUR, timeFormat.MINUTE);
        } else {
            cancel(4);
        }
        if ((flag_dow & BedTimeActivity.FLAG_THU) == BedTimeActivity.FLAG_THU) {
            long time = sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_THU, SPDefault.SETTINGS_BED_TIME_D_MILLI);
            TimeFormat timeFormat = Unit.Time.toTime(time);
            setReminder(5, timeFormat.HOUR, timeFormat.MINUTE);
        } else {
            cancel(5);
        }
        if ((flag_dow & BedTimeActivity.FLAG_FRI) == BedTimeActivity.FLAG_FRI) {
            long time = sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_FRI, SPDefault.SETTINGS_BED_TIME_D_MILLI);
            TimeFormat timeFormat = Unit.Time.toTime(time);
            setReminder(6, timeFormat.HOUR, timeFormat.MINUTE);
        } else {
            cancel(6);
        }
        if ((flag_dow & BedTimeActivity.FLAG_SAT) == BedTimeActivity.FLAG_SAT) {
            long time = sps.getLong(Config.KEY__SETTINGS_BED_TIME_D_SAT, SPDefault.SETTINGS_BED_TIME_D_MILLI);
            TimeFormat timeFormat = Unit.Time.toTime(time);
            setReminder(7, timeFormat.HOUR, timeFormat.MINUTE);
        } else {
            cancel(7);
        }
    }

}
