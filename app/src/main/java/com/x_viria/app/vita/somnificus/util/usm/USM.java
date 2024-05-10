package com.x_viria.app.vita.somnificus.util.usm;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class USM {

    private Context CONTEXT;
    private UsageStatsManager USM;

    public USM(Context context) {
        this.CONTEXT = context;
        this.USM = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public List<USMFormat> getUsageStatsEvent(long beginTime, long endTime) {
        List<USMFormat> list = new ArrayList<>();
        PackageManager pm = CONTEXT.getPackageManager();
        UsageEvents usageEvents = USM.queryEvents(beginTime, endTime);
        UsageEvents.Event event = new UsageEvents.Event();
        while (usageEvents.getNextEvent(event)) {
            USMFormat format = new USMFormat(event.getPackageName());
            ApplicationInfo appInfo;
            try {
                appInfo = pm.getApplicationInfo(event.getPackageName(), PackageManager.GET_META_DATA | PackageManager.MATCH_UNINSTALLED_PACKAGES);
                format.appName = pm.getApplicationLabel(appInfo).toString();
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            format.category = appInfo.category;
            format.eventType = event.getEventType();
            format.timestamp = event.getTimeStamp();
            list.add(format);
        }
        return list;
    }

}