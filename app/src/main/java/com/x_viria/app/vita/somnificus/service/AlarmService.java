package com.x_viria.app.vita.somnificus.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.WakeupActivity;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.util.notification.Channel;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AlarmService extends Service {

    private Vibrator VIBRATOR = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("onStartCommand", "called");

        int id = intent.getIntExtra("id", -1);

        Intent intent2 = new Intent(this, WakeupActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0xFF, intent2, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(this, Channel.ID.Alarm);
        builder.setContentTitle("Somnificus");
        builder.setContentText(getString(R.string.notification_channel_name__alarm));
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

        if (new SPStorage(this).getBool(Config.KEY__SETTINGS_ALARM_VIBRATE, SPDefault.SETTINGS_ALARM_VIBRATE)) {
            VibrationEffect effect = VibrationEffect.createWaveform(
                    new long[] {1000, 1000},
                    new int[] {255, 0},
                    0
            );
            VIBRATOR = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            VIBRATOR.vibrate(effect);
        }

        Intent intent3 = new Intent(this, PlaySoundService.class);
        intent3.putExtra(PlaySoundService.PUT_EXTRA__SOUND_TYPE, PlaySoundService.SOUND_TYPE__ALARM);
        try {
            AlarmSchedule alarmSchedule = new AlarmSchedule(getApplicationContext());
            JSONObject object = alarmSchedule.getSchedule(id);
            JSONObject objdata = object.getJSONObject("data");
            boolean opt_giv = objdata.getJSONObject("option").getBoolean("gra_increase_vol");
            if (opt_giv) intent3.putExtra(PlaySoundService.PUT_EXTRA__SOUND_OPT__FADEIN, true);
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        startForegroundService(intent3);

        startActivity(intent2);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(ServiceId.SERVICE_ID__ALARM_SERVICE);
        if (VIBRATOR != null) VIBRATOR.cancel();
    }

}
