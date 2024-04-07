package com.x_viria.app.vita.somnificus.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.x_viria.app.vita.somnificus.service.AlarmService;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent(context, AlarmService.class);
        context.startForegroundService(intent2);
    }

}
