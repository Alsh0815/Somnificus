package com.x_viria.app.vita.somnificus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Booted.", Toast.LENGTH_SHORT).show();
        Log.d("StartupReceiver", "Booted.");
    }
}