package com.x_viria.app.vita.somnificus.core;

import android.app.AlarmManager;
import android.content.Context;
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

}
