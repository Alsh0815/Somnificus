package com.x_viria.app.vita.somnificus;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.x_viria.app.vita.somnificus.util.notification.Channel;

import java.util.ArrayList;
import java.util.List;

public class Somnificus extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        List<NotificationChannel> channelList = new ArrayList<>();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel_alarm = new NotificationChannel(
                Channel.ID.Alarm,
                getString(R.string.notification_channel_name__alarm),
                NotificationManager.IMPORTANCE_HIGH
        );
        channelList.add(channel_alarm);
        NotificationChannel channel_timer = new NotificationChannel(
                Channel.ID.Timer,
                getString(R.string.notification_channel_name__timer),
                NotificationManager.IMPORTANCE_LOW
        );
        channelList.add(channel_timer);
        NotificationChannel channel_remind = new NotificationChannel(
                Channel.ID.Remind,
                getString(R.string.notification_channel_name__remind),
                NotificationManager.IMPORTANCE_LOW
        );
        channelList.add(channel_remind);
        notificationManager.createNotificationChannels(channelList);
    }

}
