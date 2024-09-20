package com.x_viria.app.vita.somnificus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.x_viria.app.vita.somnificus.core.BootEvent;

public class UpdateBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) return;
        BootEvent.boot(context);
    }

}
