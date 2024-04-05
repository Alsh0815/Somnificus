package com.x_viria.app.vita.somnificus.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.x_viria.app.vita.somnificus.activity.WakeupActivity;

public class AlarmBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent activity = new Intent(context, WakeupActivity.class);
        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activity);
    }

}
