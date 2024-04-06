package com.x_viria.app.vita.somnificus.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.activity.MainActivity;
import com.x_viria.app.vita.somnificus.activity.WakeupActivity;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmBroadcastReceiver", "Wake up");
        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
        Intent activity = new Intent(context, WakeupActivity.class);
        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activity);
    }

}
