package com.x_viria.app.vita.somnificus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.x_viria.app.vita.somnificus.service.AlarmService;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive", "called");
        Intent intent2 = new Intent(context, AlarmService.class);
        intent2.putExtra("id", intent.getIntExtra("id", -1));
        context.startForegroundService(intent2);
    }

}
