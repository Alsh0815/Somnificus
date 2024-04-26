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

    public static final String PUT_EXTRA__SOUND_TYPE = "sound_type";

    public static final String PUT_EXTRA__SOUND_OPT__FADEIN = "sound_opt__fadein";

    public static final int SOUND_TYPE__ALARM = 0x0001;
    public static final int SOUND_TYPE__TIMER = 0x0002;

    public static final int SOUND_OPT__FADEIN =0x0101;

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

        int sound_type = intent.getIntExtra(PUT_EXTRA__SOUND_TYPE, SOUND_TYPE__ALARM);

        boolean opt_fadein = intent.getBooleanExtra(PUT_EXTRA__SOUND_OPT__FADEIN, false);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            MEDIA_PLAYER = new MediaPlayer();
            MEDIA_PLAYER.setDataSource(this, uri);
            MEDIA_PLAYER.setAudioStreamType(AudioManager.STREAM_ALARM);
            MEDIA_PLAYER.setLooping(true);
            if (opt_fadein) MEDIA_PLAYER.setVolume(0.0f, 0.0f);
            MEDIA_PLAYER.prepare();
            MEDIA_PLAYER.start();
            if (opt_fadein) {
                long fade_res = 50;
                new Thread(() -> {
                    try {
                        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
                        float limitVol = ((float) am.getStreamVolume(AudioManager.STREAM_ALARM)) / ((float) am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
                        long millisec = 20000;
                        float vol = 0.0f;
                        long n = millisec / fade_res;
                        for (int i = 0; i < n; i++) {
                            vol += limitVol / n;
                            Thread.sleep(fade_res);
                            MEDIA_PLAYER.setVolume(vol, vol);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
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
