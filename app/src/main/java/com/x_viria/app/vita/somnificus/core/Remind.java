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
import com.x_viria.app.vita.somnificus.activity.SetSleepDurationActivity;
import com.x_viria.app.vita.somnificus.util.notification.Channel;

public class Remind {

    public static void SaveSleepDataNotification(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        Intent intent2 = new Intent(context, SetSleepDurationActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0xA560, intent2, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(context, Channel.ID.Remind);
        builder.setContentTitle(context.getString(R.string.notification_remind__save_sd_title));
        builder.setContentText(context.getString(R.string.notification_remind__save_sd_msg));
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_menu_alarm);
        NotificationManagerCompat.from(context).notify(0xA001, builder.build());
    }

}
