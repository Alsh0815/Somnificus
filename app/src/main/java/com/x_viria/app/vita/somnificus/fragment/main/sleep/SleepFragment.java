package com.x_viria.app.vita.somnificus.fragment.main.sleep;

import androidx.lifecycle.ViewModelProvider;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.ui.StatsGraphView;
import com.x_viria.app.vita.somnificus.util.usm.USM;
import com.x_viria.app.vita.somnificus.util.usm.USMFormat;

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
            StatsGraphView statsView = (StatsGraphView) ROOT.findViewById(R.id.SleepFragment__StatsGraph);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -7);
            calendar.set(Calendar.HOUR_OF_DAY, 0);

            USM cusm = new USM(requireContext());
            List<USMFormat> usmlist = cusm.getUsageStatsEvent(calendar.getTimeInMillis(), System.currentTimeMillis());

        }).start();
    }

    private void refreshUI() {
        StatsGraphView statsGraphView = ROOT.findViewById(R.id.SleepFragment__StatsGraph);
        TextView TextView_NoPermission = ROOT.findViewById(R.id.SleepFragment__Activity_No_Permission);
        TextView_NoPermission.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        TextView_NoPermission.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        });

        if (!checkReadStatsPermission()) {
            statsGraphView.setVisibility(View.GONE);
            TextView_NoPermission.setVisibility(View.VISIBLE);
        } else {
            statsGraphView.setVisibility(View.VISIBLE);
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