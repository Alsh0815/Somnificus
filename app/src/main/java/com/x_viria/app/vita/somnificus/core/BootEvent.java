package com.x_viria.app.vita.somnificus.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.util.notification.Channel;

import org.json.JSONException;

import java.io.IOException;

public class BootEvent {

    public static void boot(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context, Channel.ID.SystemNotifications);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(context.getString(R.string.receiver_boot_broadcast__notification_starting));
        builder.setOngoing(true);
        builder.setProgress(0, 0, true);
        builder.setSmallIcon(R.drawable.ic_menu_alarm);
        Notification notification = builder.build();

        notificationManager.notify(1, notification);

        new Thread(() -> {
            try {
                new AlarmSchedule(context).sync();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
            notificationManager.cancel(1);
        }).start();
    }

}
