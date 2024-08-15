package com.x_viria.app.vita.somnificus.fragment.main.sleep;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.SetSleepDurationActivity;
import com.x_viria.app.vita.somnificus.activity.SleepDurationActivity;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmInfo;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationInfo;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationManager;
import com.x_viria.app.vita.somnificus.core.ui.sdview.SDL7DGraphView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SleepFragment extends Fragment {

    private SleepViewModel mViewModel;

    private View ROOT;

    private long SET_WAKEUP_TIME = -1;

    public static SleepFragment newInstance() {
        return new SleepFragment();
    }

    private long getDaysDiff(Calendar targetDate) {
        Calendar today = Calendar.getInstance();
        long diffInMillis = today.getTimeInMillis() - targetDate.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(diffInMillis);
    }

    private void refreshUI() {
        LinearLayout LL_BedTime = ROOT.findViewById(R.id.SleepFragment__Bed_Time);
        LL_BedTime.setOnClickListener(v -> {
            // P
        });

        LinearLayout LL_WakeUpTime = ROOT.findViewById(R.id.SleepFragment__WakeUp_Time);
        LL_WakeUpTime.setOnClickListener(v -> {
            if (SET_WAKEUP_TIME == -1) {
                Toast.makeText(getContext(), getString(R.string.fragment_main_sleep__toast_no_alarm_24hours), Toast.LENGTH_SHORT).show();
            } else {
                Date d = new Date();
                d.setTime(SET_WAKEUP_TIME);
                SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                Toast.makeText(
                        getContext(),
                        String.format(getString(R.string.fragment_main_sleep__toast_wakeup_time), f.format(d)),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        try {
            AlarmSchedule alarmSchedule = new AlarmSchedule(requireContext());
            AlarmInfo nextAlarm = alarmSchedule.getNextAlarm(true);
            if (nextAlarm != null) {
                if (nextAlarm.getNextTime() - System.currentTimeMillis() < 24 * 60 * 60 * 1000) {
                    Date d = new Date();
                    d.setTime(nextAlarm.getNextTime());
                    SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                    TextView T1 = ROOT.findViewById(R.id.SleepFragment__Schedule_WakeUp_Time_T1);
                    T1.setTextColor(getResources().getColor(R.color.white));
                    TextView T2 = ROOT.findViewById(R.id.SleepFragment__Schedule_WakeUp_Time_T2);
                    T2.setText(f.format(d));
                    T2.setTextColor(getResources().getColor(R.color.white));
                    SET_WAKEUP_TIME = nextAlarm.getNextTime();
                }
            }
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            TextView TV__SD_Avg_Time = ROOT.findViewById(R.id.SleepFragment__SD_Avg_Time);
            SleepDurationManager sdm = new SleepDurationManager(getContext());
            List<SleepDurationInfo> sdlist = sdm.get(SleepDurationManager.Period.LAST_7DAYS);

            long[] duration = new long[7];
            for (SleepDurationInfo sdi : sdlist) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(sdi.getWakeupTime());
                long d = sdi.getWakeupTime() - sdi.getBedTime();
                int diff = (int) getDaysDiff(c);
                duration[6 - diff] += d;
            }

            int n = 0;
            for (long d : duration) {
                if (0 < d) n++;
            }

            long totalTime = 0;
            for (SleepDurationInfo sdi : sdlist) {
                totalTime += sdi.getWakeupTime() - sdi.getBedTime();
            }

            if (n == 0) n = 1;
            long avgTime = totalTime / n;
            long sec = avgTime / 1000;
            long hour = sec / 3600;
            long min = (sec % 3600) / 60;

            TV__SD_Avg_Time.setText(String.format(getString(R.string.fragment_main_sleep__text_sd_time), hour, min));

            SDL7DGraphView graph = ROOT.findViewById(R.id.SleepFragment__SDL7Graph);
            graph.max(9 * 60 * 60 * 1000);
            graph.set(duration);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ROOT = inflater.inflate(R.layout.fragment_main_sleep, container, false);

        LinearLayout LLBtn_SD = ROOT.findViewById(R.id.SleepFragment__SD_Card);
        LLBtn_SD.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SleepDurationActivity.class);
            startActivity(intent);
        });

        ImageView IVBtn_Add_SD = ROOT.findViewById(R.id.SleepFragment__SD_Add);
        IVBtn_Add_SD.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SetSleepDurationActivity.class);
            startActivity(intent);
        });

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