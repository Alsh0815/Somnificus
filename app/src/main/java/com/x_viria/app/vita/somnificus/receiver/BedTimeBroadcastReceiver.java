package com.x_viria.app.vita.somnificus.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.BedTimeManager;
import com.x_viria.app.vita.somnificus.util.Unit;
import com.x_viria.app.vita.somnificus.util.notification.Channel;

public class BedTimeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long min = intent.getLongExtra("MINUTE", -1);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context, Channel.ID.Remind);
        builder.setContentTitle(context.getString(R.string.notification_remind__bedtime_title));
        builder.setContentText(String.format(
                context.getString(R.string.notification_remind__bedtime_msg),
                Unit.Time.toMinutes(min)
        ));
        builder.setSmallIcon(R.drawable.ic_menu_alarm);
        Notification notification = builder.build();

        notificationManager.notify(1, notification);

        new BedTimeManager(context).sync();
    }

}
