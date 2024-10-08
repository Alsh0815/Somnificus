package com.x_viria.app.vita.somnificus.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.MainActivity;
import com.x_viria.app.vita.somnificus.util.notification.Channel;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    public static final String PUT_EXTRA__TIME_MS = "time_ms";

    private boolean IS_FINISHED = false;
    private MediaPlayer MEDIA_PLAYER = null;
    private Timer TIMER = new Timer();
    private Vibrator VIBRATOR = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onFinished() {
        IS_FINISHED = true;
        if (new SPStorage(this).getBool(Config.KEY__SETTINGS_TIMER_VIBRATE, SPDefault.SETTINGS_TIMER_VIBRATE)) {
            VibrationEffect effect = VibrationEffect.createWaveform(
                    new long[] {70, 200, 70, 1200},
                    new int[] {200, 0, 255, 0},
                    0
            );
            VIBRATOR = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            VIBRATOR.vibrate(effect);
        }
        if (new SPStorage(this).getBool(Config.KEY__SETTINGS_TIMER_SOUND, SPDefault.SETTINGS_TIMER_SOUND)) {
            try {
                MEDIA_PLAYER = new MediaPlayer();
                AssetFileDescriptor afd = getAssets().openFd("sounds/timer_01.mp3");
                MEDIA_PLAYER.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                MEDIA_PLAYER.setAudioStreamType(AudioManager.STREAM_ALARM);
                MEDIA_PLAYER.setLooping(true);
                afd.close();
                MEDIA_PLAYER.prepare();
                MEDIA_PLAYER.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this, Channel.ID.Timer);
        builder.setContentTitle("Somnificus");
        builder.setContentText(getString(R.string.notification_channel_name__timer));
        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(ServiceId.SERVICE_ID__TIMER_SERVICE, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(ServiceId.SERVICE_ID__TIMER_SERVICE, notification);
            }
        }

        long now_ms = System.currentTimeMillis();
        long time_ms = intent.getLongExtra(PUT_EXTRA__TIME_MS, 0);
        long target_time = now_ms + time_ms;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                intent1.putExtra(MainActivity.PUT_EXTRA__SET_FRAGMENT, MainActivity.SET_FRAGMENT__TIMER);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        getApplicationContext(),
                        0xff,
                        intent1,
                        PendingIntent.FLAG_IMMUTABLE
                );
                long rem_ms = target_time - System.currentTimeMillis();
                long sec = (rem_ms / 1000) % 60;
                long min = (rem_ms / (1000 * 60)) % 60;
                long hour = (rem_ms / (1000 * 60 * 60)) % 24;
                builder.setContentTitle(String.format("Somnificus - %s", getString(R.string.notification_channel_name__timer)));
                if (rem_ms < 0) {
                    builder.setContentText("00:00");
                    if (!IS_FINISHED) onFinished();
                } else if (3600000 <= rem_ms) {
                    builder.setContentText(String.format("%02d:%02d:%02d", hour, min, sec));
                } else {
                    builder.setContentText(String.format("%02d:%02d", min, sec));
                }
                builder.setSmallIcon(R.drawable.ic_menu_timer);
                builder.setContentIntent(pendingIntent);
                Notification notification = builder.build();
                notificationManager.notify(ServiceId.SERVICE_ID__TIMER_SERVICE, notification);
            }
        };
        TIMER.scheduleAtFixedRate(timerTask, 0, 100);

        new SPStorage(getApplicationContext()).setLong(Config.TMP__TIMER_VAL, target_time);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TIMER.cancel();
        if (MEDIA_PLAYER != null) {
            MEDIA_PLAYER.stop();
            MEDIA_PLAYER = null;
        }
        if (VIBRATOR != null) {
            VIBRATOR.cancel();
            VIBRATOR = null;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(ServiceId.SERVICE_ID__TIMER_SERVICE);
    }
}
