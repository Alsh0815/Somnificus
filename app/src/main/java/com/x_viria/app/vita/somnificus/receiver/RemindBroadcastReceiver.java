package com.x_viria.app.vita.somnificus.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.MainActivity;
import com.x_viria.app.vita.somnificus.util.notification.Channel;

public class RemindBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra("type", RemindType.UNKNOWN);
        Log.d("RemindBroadcastReceiver", "onReceive() [type = " + type + "]");

        switch (type) {
            case RemindType.SET_ALARM:
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(context, Channel.ID.Remind);
                builder.setAutoCancel(true);
                builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
                builder.setContentTitle(context.getString(R.string.notification_remind__set_alarm_title));
                builder.setContentText(context.getString(R.string.notification_remind__set_alarm_msg));
                builder.setSmallIcon(R.drawable.ic_menu_alarm);
                Notification notification = builder.build();
                notificationManager.notify(1, notification);
                break;
            default:
                break;
        }
    }

    public static class RemindType {

        public static final int SET_ALARM = 0x01;
        public static final int UNKNOWN = 0xFF;

    }

}
