package com.x_viria.app.vita.somnificus.core;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.MainActivity;
import com.x_viria.app.vita.somnificus.activity.SetSleepDurationActivity;
import com.x_viria.app.vita.somnificus.util.notification.Channel;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import java.time.LocalDateTime;
import java.util.Random;

public class Remind {

    public static void MissedAlarm(Context context, LocalDateTime date) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        Random rand = new Random();
        int id = rand.nextInt(NotificationID.REMIND__MISSED_ALARM_MAX - NotificationID.REMIND__MISSED_ALARM_MIN + 1) + NotificationID.REMIND__MISSED_ALARM_MIN;
        Notification.Builder builder = new Notification.Builder(context, Channel.ID.Remind);
        builder.setAutoCancel(true);
        builder.setContentTitle(context.getString(R.string.notification_remind__missed_alarm_title));
        builder.setContentText(
                String.format(context.getString(R.string.notification_remind__missed_alarm_msg), date.getHour(), date.getMinute())
        );
        builder.setSmallIcon(R.drawable.ic_menu_alarm);
        NotificationManagerCompat.from(context).notify(id, builder.build());
    }

    public static void NoAlarmIsSet(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0xA561, intent2, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(context, Channel.ID.Remind);
        builder.setAutoCancel(true);
        builder.setContentTitle(context.getString(R.string.notification_remind__no_alarm_is_set_title));
        builder.setContentText(context.getString(R.string.notification_remind__no_alarm_is_set_msg));
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_menu_alarm);
        NotificationManagerCompat.from(context).notify(NotificationID.REMIND__NO_ALARM_IS_SET, builder.build());
    }

    public static void SaveSleepDataNotification(Context context) {
        SPStorage spStorage = new SPStorage(context);
        boolean enable = spStorage.getBool(Config.KEY__SETTINGS_REMIND_SAVESD, SPDefault.SETTINGS_REMIND_SAVESD);
        if (!enable) return;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        Intent intent2 = new Intent(context, SetSleepDurationActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0xA560, intent2, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(context, Channel.ID.Remind);
        builder.setAutoCancel(true);
        builder.setContentTitle(context.getString(R.string.notification_remind__save_sd_title));
        builder.setContentText(context.getString(R.string.notification_remind__save_sd_msg));
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_menu_alarm);
        NotificationManagerCompat.from(context).notify(NotificationID.REMIND__SAVE_SD, builder.build());
    }

}
