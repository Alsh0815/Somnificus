package com.x_viria.app.vita.somnificus.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;

public class Alarm {

    private static AlarmManager getAM(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static boolean canScheduleExactAlarms(Context context) {
        AlarmManager alarmManager = getAM(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return alarmManager.canScheduleExactAlarms();
        } else {
            return true;
        }
    }

    public static void setAlarm(Context context, AlarmInfo info) {
        int id = 0;
        AlarmManager alarmManager = getAM(context);
        Intent intent = new Intent(context.getApplicationContext(), AlarmBroadcastReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id", id);
        intent.putExtra("title", "Test Alarm");
        PendingIntent pending = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager.AlarmClockInfo acInfo = new AlarmManager.AlarmClockInfo(info.getCalendar().getTimeInMillis(), null);
        alarmManager.setAlarmClock(acInfo, pending);
    }

}
