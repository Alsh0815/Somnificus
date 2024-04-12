package com.x_viria.app.vita.somnificus.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.WakeupActivity;

public class AlarmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String channel_id = "somnificus_notification__alarm";
        NotificationChannel channel = new NotificationChannel(
                channel_id,
                "Alarm",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        Intent intent2 = new Intent(this, WakeupActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0xFF, intent2, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(this, channel_id);
        builder.setContentTitle("Somnificus");
        builder.setContentText("Alarm");
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_menu_alarm);
        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(ServiceId.SERVICE_ID__ALARM_SERVICE, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(ServiceId.SERVICE_ID__ALARM_SERVICE, notification);
            }
        }

        Intent intent3 = new Intent(this, PlaySoundService.class);
        intent3.putExtra(PlaySoundService.PUT_EXTRA__SOUND_TYPE, PlaySoundService.SOUND_TYPE__ALARM);
        startForegroundService(intent3);

        startActivity(intent2);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(ServiceId.SERVICE_ID__ALARM_SERVICE);
    }

}
