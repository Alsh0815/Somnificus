package com.x_viria.app.vita.somnificus.util;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.icu.util.Calendar;

import java.util.List;

public class UsageStats {

    private final Context CONTEXT;

    public UsageStats(Context context) {
        this.CONTEXT = context;
    }

    public List<android.app.usage.UsageStats> getUsageStatsObject(long beginTime) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) CONTEXT.getSystemService(Context.USAGE_STATS_SERVICE);

        return usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                beginTime,
                System.currentTimeMillis());
    }

}
