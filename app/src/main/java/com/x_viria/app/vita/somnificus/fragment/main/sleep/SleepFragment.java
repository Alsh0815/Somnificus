package com.x_viria.app.vita.somnificus.fragment.main.sleep;

import androidx.lifecycle.ViewModelProvider;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepFragment extends Fragment {

    private SleepViewModel mViewModel;

    private View ROOT;

    public static SleepFragment newInstance() {
        return new SleepFragment();
    }

    private boolean checkReadStatsPermission() {
        AppOpsManager aom = (AppOpsManager) requireContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = aom.checkOp(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), requireContext().getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            return requireContext().checkPermission("android.permission.PACKAGE_USAGE_STATS", android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void refreshActivityList() {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            PackageManager pm = requireActivity().getPackageManager();
            LinearLayout parent = ROOT.findViewById(R.id.SleepFragment__Activity);
            handler.post(parent::removeAllViews);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            List<UsageStats> usageStats = new com.x_viria.app.vita.somnificus.util.UsageStats(getContext()).getUsageStatsObject(calendar.getTimeInMillis());
            for (int i = 0; i < usageStats.size(); i++) {
                long mills = usageStats.get(i).getTotalTimeInForeground();
                if (mills == 0) continue;
                long sec = (mills / 1000) % 60;
                long min = (mills / (1000 * 60)) % 60;
                long hour = (mills / (1000 * 60 * 60)) % 24;
                String appName = "";
                try {
                    appName = pm.getApplicationLabel(
                            pm.getApplicationInfo(usageStats.get(i).getPackageName(), PackageManager.GET_META_DATA)
                    ).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    appName = usageStats.get(i).getPackageName();
                }
                final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE);
                final Date date = new Date(usageStats.get(i).getFirstTimeStamp());
                TextView view = new TextView(getContext());
                String finalAppName = appName;
                handler.post(() -> {
                    view.setText(
                            String.format(
                                    "[%s] %s %02d:%02d:%02d",
                                    finalAppName,
                                    df.format(date),
                                    hour,
                                    min,
                                    sec
                            )
                    );
                    parent.addView(view);
                });
            }
        }).start();
    }

    private void refreshUI() {
        TextView TextView_NoPermission = ROOT.findViewById(R.id.SleepFragment__Activity_No_Permission);
        TextView_NoPermission.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        TextView_NoPermission.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        });

        if (!checkReadStatsPermission()) {
            TextView_NoPermission.setVisibility(View.VISIBLE);
        } else {
            TextView_NoPermission.setVisibility(View.GONE);
            refreshActivityList();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ROOT = inflater.inflate(R.layout.fragment_main_sleep, container, false);

        refreshUI();

        return ROOT;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshUI();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SleepViewModel.class);
    }

}