package com.x_viria.app.vita.somnificus.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;

public class PlaySoundService extends Service {

    private MediaPlayer MEDIA_PLAYER = null;

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

        Notification.Builder builder = new Notification.Builder(this, channel_id);
        builder.setContentTitle("Somnificus");
        builder.setContentText("Alarm");
        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(ServiceId.SERVICE_ID__PLAY_SOUND_SERVICE, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else {
                startForeground(ServiceId.SERVICE_ID__PLAY_SOUND_SERVICE, notification);
            }
        }

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            MEDIA_PLAYER = new MediaPlayer();
            MEDIA_PLAYER.setDataSource(this, uri);
            MEDIA_PLAYER.setAudioStreamType(AudioManager.STREAM_ALARM);
            MEDIA_PLAYER.setLooping(true);
            MEDIA_PLAYER.prepare();
            MEDIA_PLAYER.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (MEDIA_PLAYER != null) {
            MEDIA_PLAYER.stop();
            MEDIA_PLAYER = null;
        }
    }
}
